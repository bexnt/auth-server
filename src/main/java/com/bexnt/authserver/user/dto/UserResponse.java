package com.bexnt.authserver.user.dto;

import com.bexnt.authserver.user.entity.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
