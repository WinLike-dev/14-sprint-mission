package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response;

import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽음 상태 응답 DTO.
 * id를 함께 알린다. 클라이언트는 이 id로 갱신 대상을 지정한다.
 */
public record ReadStatusDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
    public static ReadStatusDto from(ReadStatus status) {
        return new ReadStatusDto(
                status.getId(),
                status.getCreatedAt(),
                status.getUpdatedAt(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt()
        );
    }
}
