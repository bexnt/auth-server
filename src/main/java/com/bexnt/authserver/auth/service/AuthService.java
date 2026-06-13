package com.bexnt.authserver.auth.service;

import com.bexnt.authserver.auth.dto.AuthResponse;
import com.bexnt.authserver.auth.dto.LoginRequest;
import com.bexnt.authserver.auth.dto.RegisterRequest;
import com.bexnt.authserver.exception.EmailAlreadyExistsException;
import com.bexnt.authserver.exception.InvalidCredentialsException;
import com.bexnt.authserver.security.jwt.JwtService;
import com.bexnt.authserver.user.entity.Role;
import com.bexnt.authserver.user.entity.UserEntity;
import com.bexnt.authserver.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        String token = jwtService.generateToken(request.email());

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse("temporary-token", "Bearer");
    }
}
