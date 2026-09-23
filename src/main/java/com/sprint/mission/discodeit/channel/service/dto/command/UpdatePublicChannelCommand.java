package com.sprint.mission.discodeit.channel.service.dto.command;

public record UpdatePublicChannelCommand(
        String newName,
        String newDescription
) {
}
