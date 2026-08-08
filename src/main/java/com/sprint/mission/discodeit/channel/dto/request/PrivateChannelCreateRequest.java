package com.sprint.mission.discodeit.channel.dto.request;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PrivateChannelCreateRequest(List<UUID> participantIds) {

    public PrivateChannelCreateRequest {
        participantIds = List.copyOf(Objects.requireNonNull(participantIds));
    }
}
