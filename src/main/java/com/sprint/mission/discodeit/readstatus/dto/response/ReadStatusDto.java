package com.sprint.mission.discodeit.readstatus.dto.response;

import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(UUID userId, UUID channelId, Instant lastReadAt) {

    public static ReadStatusDto from(ReadStatus status) {
        return new ReadStatusDto(status.getUserId(), status.getChannelId(), status.getLastReadAt());
    }
}
