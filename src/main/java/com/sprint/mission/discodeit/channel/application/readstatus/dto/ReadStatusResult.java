package com.sprint.mission.discodeit.channel.application.readstatus.dto;

import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResult(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
    public static ReadStatusResult from(ReadStatus status) {
        return new ReadStatusResult(
                status.getId(),
                status.getCreatedAt(),
                status.getUpdatedAt(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt()
        );
    }
}
