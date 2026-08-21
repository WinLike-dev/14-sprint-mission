package com.sprint.mission.discodeit.message.application.message.dto;

import java.util.List;
import java.util.UUID;

/**
 * 메시지 생성 유스케이스 입력.
 * REST MessageCreateRequest를 그대로 쓰지 않는다.
 * 첨부파일은 multipart의 별도 파트로 오므로 요청 본문 DTO가 담을 수 없고,
 * 컨트롤러가 두 파트를 합쳐 이 입력을 만든다.
 */
public record CreateMessageCommand(
        String content,
        UUID channelId,
        UUID authorId,
        List<MessageAttachmentCommand> attachments
) {
    public CreateMessageCommand {
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
    }
}
