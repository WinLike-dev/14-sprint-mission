package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response;

import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽음 상태 응답 DTO.
 *
 * id는 저장된 값을 그대로 알리는 것일 뿐, 갱신 대상을 지정하는 데 쓰이지 않는다.
 * 대상은 (userId, channelId)로 지정하므로 클라이언트가 id를 보관할 이유가 없다.
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
