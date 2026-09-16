package com.sprint.mission.discodeit.user.service.dto;

import com.sprint.mission.discodeit.user.entity.UserStatus;

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
                // 지연 로딩 프록시의 id는 초기화 없이 읽을 수 있어 추가 쿼리가 나가지 않는다.
                status.getUser().getId(),
                status.getLastActiveAt(),
                status.isOnline()
        );
    }
}
