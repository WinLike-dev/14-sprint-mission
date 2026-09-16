package com.sprint.mission.discodeit.message.service.dto;

import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.message.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResult(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds
) {
    public MessageResult {
        attachmentIds = List.copyOf(attachmentIds);
    }

    public static MessageResult from(Message message) {
        return new MessageResult(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                // 작성자가 탈퇴하면 null이 된다. 프록시의 id는 초기화 없이 읽는다.
                message.getAuthor() == null ? null : message.getAuthor().getId(),
                message.getAttachments().stream().map(BinaryContent::getId).toList()
        );
    }
}
