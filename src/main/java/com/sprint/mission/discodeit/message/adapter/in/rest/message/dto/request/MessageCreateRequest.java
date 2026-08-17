package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 메시지 생성 요청 DTO.
 * 클라이언트가 새 메시지를 보낼 때 필요한 정보를 담는다.
 * record를 사용하여 불변 객체로 만든다.
 */
public record MessageCreateRequest(
        String content,         // 메시지 본문
        UUID channelId,         // 메시지를 보낼 채널의 ID
        UUID authorId,          // 메시지 작성자의 ID
        List<MessageAttachmentCreateRequest> attachments // 첨부파일 목록
) {

    // 컴팩트 생성자: 첨부파일 목록을 불변 리스트로 복사하여 외부 변경을 방지한다
    public MessageCreateRequest {
        attachments = List.copyOf(Objects.requireNonNull(attachments));
    }
}
