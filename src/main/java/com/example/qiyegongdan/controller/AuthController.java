package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.dto.LoginRequest;
import com.example.qiyegongdan.dto.LoginResponse;
import com.example.qiyegongdan.dto.UserCreateRequest;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.service.AuthService;
import com.example.qiyegongdan.vo.ApiResponse;
import com.example.qiyegongdan.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录注册")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<UserVO> register(@Valid @RequestBody UserCreateRequest request) {
        User user = authService.register(request);
        return ApiResponse.success(UserVO.builder()
                .id(user.getId()).username(user.getUsername())
                .realName(user.getRealName()).email(user.getEmail())
                .phone(user.getPhone()).role(user.getRole())
                .enabled(user.getEnabled()).createdAt(user.getCreatedAt())
                .build());
    }
}