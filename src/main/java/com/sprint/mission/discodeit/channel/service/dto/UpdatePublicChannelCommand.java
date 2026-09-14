package com.sprint.mission.discodeit.channel.service.dto;

public record UpdatePublicChannelCommand(
        String newName,
        String newDescription
) {
}
