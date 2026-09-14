package com.sprint.mission.discodeit.channel.service.dto;

import java.time.Instant;

public record UpdateReadStatusCommand(Instant lastReadAt) {
}
