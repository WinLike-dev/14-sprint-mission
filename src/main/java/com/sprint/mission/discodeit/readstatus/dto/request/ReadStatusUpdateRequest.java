package com.sprint.mission.discodeit.readstatus.dto.request;

import java.time.Instant;

public record ReadStatusUpdateRequest(Instant lastReadAt) {
}
