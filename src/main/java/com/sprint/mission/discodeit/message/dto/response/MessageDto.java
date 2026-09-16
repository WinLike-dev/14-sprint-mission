package com.sprint.mission.discodeit.message.dto.response;

import com.sprint.mission.discodeit.content.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.user.dto.response.UserDto;

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
        UUID id,                              // 메시지 고유 ID
        Instant createdAt,                    // 생성 시각
        Instant updatedAt,                    // 마지막 수정 시각
        String content,                       // 메시지 본문
        UUID channelId,                       // 메시지가 속한 채널 ID
        UserDto author,                       // 작성자 (탈퇴했으면 null)
        List<BinaryContentDto> attachments    // 첨부파일 목록
) {
    // 컴팩트 생성자: 첨부파일 목록을 불변 리스트로 복사한다
    public MessageDto {
        attachments = List.copyOf(attachments);
    }
}
