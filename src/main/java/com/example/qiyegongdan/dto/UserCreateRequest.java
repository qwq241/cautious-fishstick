package com.example.qiyegongdan.dto;

import com.example.qiyegongdan.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "用户创建请求")
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度须在3~50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度须在6~100之间")
    @Schema(description = "密码", example = "password123")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名最长50个字符")
    @Schema(description = "真实姓名", example = "系统管理员")
    private String realName;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotNull(message = "角色不能为空")
    @Schema(description = "用户角色", example = "ADMIN")
    private UserRole role;
}