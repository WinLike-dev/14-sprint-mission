package com.sprint.mission.discodeit.message.dto.request;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentCreateRequest> attachments
) {

    public MessageCreateRequest {
        attachments = List.copyOf(Objects.requireNonNull(attachments));
    }
}
