package com.example.qiyegongdan.config;

import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.enums.UserRole;
import com.example.qiyegongdan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .realName("系统管理员")
                .email("admin@example.com")
                .phone("13800138001")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager123"))
                .realName("工单管理员")
                .email("manager@example.com")
                .phone("13800138002")
                .role(UserRole.MANAGER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .username("engineer1")
                .password(passwordEncoder.encode("engineer123"))
                .realName("工程师张三")
                .email("engineer1@example.com")
                .phone("13800138003")
                .role(UserRole.ENGINEER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .username("engineer2")
                .password(passwordEncoder.encode("engineer123"))
                .realName("工程师李四")
                .email("engineer2@example.com")
                .phone("13800138004")
                .role(UserRole.ENGINEER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .realName("普通用户")
                .email("user@example.com")
                .phone("13800138005")
                .role(UserRole.USER)
                .enabled(true)
                .build());

        System.out.println("预置用户数据初始化完成");
    }
}