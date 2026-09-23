package com.sprint.mission.discodeit.content.service.dto.result;

import com.sprint.mission.discodeit.content.entity.BinaryContent;

import java.util.UUID;

// 바이너리 콘텐츠의 메타 정보. 실제 파일은 다운로드 API로 받는다.
public record BinaryContentResult(
        UUID id,
        String fileName,
        long size,
        String contentType
) {

    public static BinaryContentResult from(BinaryContent content) {
        if (content == null) {
            return null;
        }
        return new BinaryContentResult(
                content.getId(),
                content.getFileName(),
                content.getSize(),
                content.getContentType()
        );
    }
}
