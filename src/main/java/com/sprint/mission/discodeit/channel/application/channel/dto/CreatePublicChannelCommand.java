package com.sprint.mission.discodeit.channel.application.channel.dto;

public record CreatePublicChannelCommand(
        String name,
        String description
) {
}
