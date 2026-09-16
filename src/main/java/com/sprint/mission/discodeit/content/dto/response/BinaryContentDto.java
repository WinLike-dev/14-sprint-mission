package com.sprint.mission.discodeit.content.dto.response;

import java.util.UUID;

/**
 * 바이너리 콘텐츠 응답 DTO.
 * 클라이언트에게 첨부파일의 메타 정보를 전달한다. 실제 파일은 다운로드 API로 받는다.
 * 도메인 엔티티(BinaryContent)를 직접 노출하지 않고 DTO로 변환하여 반환한다.
 */
public record BinaryContentDto(
        UUID id,              // 고유 식별자
        String fileName,      // 파일 이름
        long size,            // 파일 크기 (바이트 단위)
        String contentType    // MIME 타입
) {
}
