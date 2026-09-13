package com.sprint.mission.discodeit.channel.application.channel.dto;

import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ChannelResult(
        UUID id,
        ChannelType type,
        String name,
        String description,
        List<UUID> participantIds,
        Instant lastMessageAt
) {

    public ChannelResult {
        participantIds = List.copyOf(
                Objects.requireNonNull(
                        participantIds,
                        "participantIds는 null일 수 없습니다."
                )
        );
    }

    public static ChannelResult from(Channel channel, List<UUID> participantIds) {
        Objects.requireNonNull(channel, "channel은 null일 수 없습니다.");

        return new ChannelResult(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                channel.getLastMessageAt()
        );
    }
}
