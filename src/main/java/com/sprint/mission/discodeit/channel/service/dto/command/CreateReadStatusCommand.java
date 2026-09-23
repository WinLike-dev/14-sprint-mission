package com.sprint.mission.discodeit.channel.service.dto.command;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusCommand(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
