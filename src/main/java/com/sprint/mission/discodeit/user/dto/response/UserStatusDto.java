package com.sprint.mission.discodeit.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 접속 상태 응답 DTO.
 * 온라인 여부는 lastActiveAt으로 그때그때 판단하는 값이라 UserDto.online으로만 알린다.
 */
public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {
}
