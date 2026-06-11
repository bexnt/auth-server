package com.bexnt.authserver.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType
) {
}
