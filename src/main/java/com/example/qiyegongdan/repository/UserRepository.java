package com.example.qiyegongdan.repository;

import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleAndEnabledTrue(UserRole role);
}