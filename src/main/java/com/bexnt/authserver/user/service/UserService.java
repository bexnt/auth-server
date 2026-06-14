package com.bexnt.authserver.user.service;

import com.bexnt.authserver.user.dto.UserResponse;
import com.bexnt.authserver.user.entity.UserEntity;
import com.bexnt.authserver.user.mapper.UserMapper;
import com.bexnt.authserver.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }
}
