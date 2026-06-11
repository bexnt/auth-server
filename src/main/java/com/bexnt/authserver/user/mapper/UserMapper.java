package com.bexnt.authserver.user.mapper;

import com.bexnt.authserver.user.dto.UserResponse;
import com.bexnt.authserver.user.entity.UserEntity;

public class UserMapper {
    private UserMapper() {
    }

    public static UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
