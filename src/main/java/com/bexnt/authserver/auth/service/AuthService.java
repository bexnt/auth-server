package com.bexnt.authserver.auth.service;

import com.bexnt.authserver.auth.dto.AuthResponse;
import com.bexnt.authserver.auth.dto.RegisterRequest;
import com.bexnt.authserver.exception.EmailAlreadyExistsException;
import com.bexnt.authserver.user.entity.Role;
import com.bexnt.authserver.user.entity.UserEntity;
import com.bexnt.authserver.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        String passwordHash = passwordEncoder.encode(request.password());

        UserEntity user = new UserEntity(
                request.email(),
                passwordHash,
                Role.USER
        );

        userRepository.save(user);
        return new AuthResponse("temporary-token", "Bearer");
    }

}
