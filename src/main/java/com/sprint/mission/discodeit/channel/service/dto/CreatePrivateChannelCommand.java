package com.sprint.mission.discodeit.channel.service.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CreatePrivateChannelCommand(
        List<UUID> participantIds
) {

    public CreatePrivateChannelCommand {
        participantIds = List.copyOf(
                Objects.requireNonNull(
                        participantIds,
                        "participantIds는 null일 수 없습니다."
                )
        );
    }
}
