package com.example.qiyegongdan.service;

import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.enums.UserRole;
import com.example.qiyegongdan.exception.BusinessException;
import com.example.qiyegongdan.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> list(int page, int size) {
        return userRepository.findAll(PageRequest.of(page - 1, size));
    }

    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    @Transactional
    public void toggleStatus(Long id) {
        User user = get(id);
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        userRepository.save(user);
    }

    public List<User> getEngineers() {
        return userRepository.findByRoleAndEnabledTrue(UserRole.ENGINEER);
    }
}