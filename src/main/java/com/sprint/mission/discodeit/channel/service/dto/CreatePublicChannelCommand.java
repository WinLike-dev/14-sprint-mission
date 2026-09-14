package com.sprint.mission.discodeit.channel.service.dto;

public record CreatePublicChannelCommand(
        String name,
        String description
) {
}
