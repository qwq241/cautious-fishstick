package com.example.qiyegongdan.vo;

import com.example.qiyegongdan.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
