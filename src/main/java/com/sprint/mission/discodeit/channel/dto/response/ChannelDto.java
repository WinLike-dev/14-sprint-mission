package com.sprint.mission.discodeit.channel.dto.response;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantIds
) {
    public ChannelDto {
        participantIds = List.copyOf(participantIds);
    }

    public static ChannelDto from(
            Channel channel,
            Instant lastMessageAt,
            List<UUID> participantIds
    ) {
        return new ChannelDto(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }
}
