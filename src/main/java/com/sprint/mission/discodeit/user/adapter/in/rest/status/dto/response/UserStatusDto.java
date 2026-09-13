package com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 접속 상태 응답 DTO.
 * online은 저장된 값이 아니라 lastActiveAt으로 그때그때 판단한 결과다.
 */
public record UserStatusDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        Instant lastActiveAt,
        boolean online
) {
}
