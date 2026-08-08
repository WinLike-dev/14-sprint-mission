package com.sprint.mission.discodeit.binarycontent.dto.response;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        Instant createdAt,
        String fileName,
        long size,
        String contentType,
        byte[] bytes
) {
    // 컴팩트 생성자
    public BinaryContentDto {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

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
