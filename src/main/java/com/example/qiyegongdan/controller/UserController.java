package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.dto.UserCreateRequest;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.security.UserPrincipal;
import com.example.qiyegongdan.service.AuthService;
import com.example.qiyegongdan.service.UserService;
import com.example.qiyegongdan.vo.ApiResponse;
import com.example.qiyegongdan.vo.PageResult;
import com.example.qiyegongdan.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户增删改查")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "用户列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<PageResult<UserVO>> list(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.list(page, size);
        List<UserVO> vos = userPage.getContent().stream().map(this::toVO).toList();
        return ApiResponse.success(new PageResult<>(vos, userPage.getTotalElements(), page, size, userPage.getTotalPages()));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        User user = authService.register(request);
        return ApiResponse.success(toVO(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<UserVO> get(@PathVariable Long id) {
        return ApiResponse.success(toVO(userService.get(id)));
    }

    @GetMapping("/me")
    @Operation(summary = "当前用户信息")
    public ApiResponse<UserVO> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(toVO(userService.get(principal.getId())));
    }

    @GetMapping("/engineers")
    @Operation(summary = "获取所有工程师列表")
    public ApiResponse<List<UserVO>> getEngineers() {
        List<UserVO> vos = userService.getEngineers().stream().map(this::toVO).toList();
        return ApiResponse.success(vos);
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "启用/禁用用户")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> toggleStatus(@PathVariable Long id) {
        userService.toggleStatus(id);
        return ApiResponse.success(null);
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId()).username(user.getUsername())
                .realName(user.getRealName()).email(user.getEmail())
                .phone(user.getPhone()).role(user.getRole())
                .enabled(user.getEnabled()).createdAt(user.getCreatedAt())
                .build();
    }
}