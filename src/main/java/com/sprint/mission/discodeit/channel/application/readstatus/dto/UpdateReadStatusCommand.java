package com.sprint.mission.discodeit.channel.application.readstatus.dto;

import java.time.Instant;

public record UpdateReadStatusCommand(Instant lastReadAt) {
}
