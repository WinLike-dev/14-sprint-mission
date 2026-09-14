package com.sprint.mission.discodeit.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 HTTP 응답 DTO.
 * 유스케이스 결과(UserResult)를 JSON 필드로만 옮긴다.
 * password 제외와 online 합치기는 UserResult가 이미 끝낸 규칙이다.
 */
public record UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String email,
        UUID profileId,
        boolean online
) {
}
