package com.sprint.mission.discodeit.channel.service.dto.command;

import java.time.Instant;

public record UpdateReadStatusCommand(Instant lastReadAt) {
}
