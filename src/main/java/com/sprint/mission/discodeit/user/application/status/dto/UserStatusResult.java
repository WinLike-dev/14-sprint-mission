package com.sprint.mission.discodeit.user.application.status.dto;

import com.sprint.mission.discodeit.user.domain.status.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResult(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        Instant lastActiveAt,
        boolean online
) {
    public static UserStatusResult from(UserStatus status) {
        return new UserStatusResult(
                status.getId(),
                status.getCreatedAt(),
                status.getUpdatedAt(),
                status.getUserId(),
                status.getLastActiveAt(),
                status.isOnline()
        );
    }
}
