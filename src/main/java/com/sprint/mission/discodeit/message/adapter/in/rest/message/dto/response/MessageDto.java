package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response;

import com.sprint.mission.discodeit.message.domain.message.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 메시지 응답 DTO.
 * 클라이언트에게 메시지 정보를 전달할 때 사용한다.
 * 도메인 엔티티(Message)를 직접 노출하지 않고 DTO로 변환하여 반환하는 이유:
 * 내부 도메인 구조가 변경되더라도 API 응답 형식은 유지할 수 있기 때문이다.
 */
public record MessageDto(
        UUID id,                    // 메시지 고유 ID
        Instant createdAt,          // 생성 시각
        Instant updatedAt,          // 마지막 수정 시각
        String content,             // 메시지 본문
        UUID channelId,             // 메시지가 속한 채널 ID
        UUID authorId,              // 작성자 ID
        List<UUID> attachmentIds    // 첨부파일 ID 목록
) {
    // 컴팩트 생성자: 첨부파일 ID 목록을 불변 리스트로 복사한다
    public MessageDto {
        attachmentIds = List.copyOf(attachmentIds);
    }

    // Message 도메인 엔티티를 MessageDto로 변환하는 팩토리 메서드
    public static MessageDto from(Message message) {
        return new MessageDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}
