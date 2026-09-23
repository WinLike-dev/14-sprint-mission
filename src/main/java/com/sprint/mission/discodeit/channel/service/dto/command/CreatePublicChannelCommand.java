package com.sprint.mission.discodeit.channel.service.dto.command;

public record CreatePublicChannelCommand(
        String name,
        String description
) {
}
