package com.example.qiyegongdan.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private Long id;
    private String username;
    private String realName;
    private String role;
}
