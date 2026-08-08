package com.sprint.mission.discodeit.userstatus.dto.response;

import com.sprint.mission.discodeit.userstatus.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(UUID userId, Instant lastActiveAt, boolean online) {

    public static UserStatusDto from(UserStatus status) {
        return new UserStatusDto(status.getUserId(), status.getLastActiveAt(), status.isOnline());
    }
}
