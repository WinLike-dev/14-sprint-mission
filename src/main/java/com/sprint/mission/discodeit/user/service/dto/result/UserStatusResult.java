package com.sprint.mission.discodeit.user.service.dto.result;

import com.sprint.mission.discodeit.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResult(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {

    public static UserStatusResult from(UserStatus status) {
        return new UserStatusResult(
                status.getId(),
                // 지연 로딩 프록시의 id는 초기화 없이 읽을 수 있어 추가 쿼리가 나가지 않는다.
                status.getUser().getId(),
                status.getLastActiveAt()
        );
    }
}
