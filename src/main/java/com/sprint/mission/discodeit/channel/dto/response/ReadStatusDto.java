package com.sprint.mission.discodeit.channel.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽음 상태 응답 DTO.
 * id를 함께 알린다. 클라이언트는 이 id로 갱신 대상을 지정한다.
 */
public record ReadStatusDto(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
