package com.sprint.mission.discodeit.userstatus.dto.request;

import java.time.Instant;

public record UserStatusUpdateRequest(Instant lastActiveAt) {
}
