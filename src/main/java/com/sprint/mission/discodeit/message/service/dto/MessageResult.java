package com.sprint.mission.discodeit.message.service.dto;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.user.service.dto.UserResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResult(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserResult author,
        List<BinaryContentResult> attachments
) {

    public MessageResult {
        attachments = List.copyOf(attachments);
    }

    public static MessageResult from(Message message) {
        return new MessageResult(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                // 지연 로딩 프록시의 id는 초기화 없이 읽을 수 있어 채널 조회가 추가로 나가지 않는다.
                message.getChannel().getId(),
                // 작성자가 탈퇴하면 null이 된다.
                message.getAuthor() == null ? null : UserResult.from(message.getAuthor()),
                message.getAttachments().stream().map(BinaryContentResult::from).toList()
        );
    }
}
