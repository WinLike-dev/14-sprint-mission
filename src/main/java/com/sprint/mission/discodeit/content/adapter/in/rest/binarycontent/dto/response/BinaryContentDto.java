package com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response;

import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 응답 DTO.
 * 클라이언트에게 첨부파일 정보를 전달할 때 사용한다.
 * 도메인 엔티티(BinaryContent)를 직접 노출하지 않고 DTO로 변환하여 반환한다.
 */
public record BinaryContentDto(
        UUID id,              // 고유 식별자
        Instant createdAt,    // 생성 시각
        String fileName,      // 파일 이름
        long size,            // 파일 크기 (바이트 단위)
        String contentType,   // MIME 타입
        byte[] bytes          // 파일의 실제 바이너리 데이터
) {
    // 컴팩트 생성자: byte 배열을 방어적으로 복사하여 데이터 무결성을 보장한다
    public BinaryContentDto {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // BinaryContent 도메인 엔티티를 BinaryContentDto로 변환하는 팩토리 메서드
    public static BinaryContentDto from(BinaryContent content) {
        return new BinaryContentDto(
                content.getId(),
                content.getCreatedAt(),
                content.getFileName(),
                content.getSize(),
                content.getContentType(),
                content.getBytes()
        );
    }
}
