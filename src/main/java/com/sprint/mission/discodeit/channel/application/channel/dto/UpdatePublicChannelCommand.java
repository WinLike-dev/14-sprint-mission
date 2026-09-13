package com.sprint.mission.discodeit.channel.application.channel.dto;

public record UpdatePublicChannelCommand(
        String newName,
        String newDescription
) {
}
