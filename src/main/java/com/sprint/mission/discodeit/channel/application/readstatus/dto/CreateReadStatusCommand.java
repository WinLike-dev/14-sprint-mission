package com.sprint.mission.discodeit.channel.application.readstatus.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusCommand(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
