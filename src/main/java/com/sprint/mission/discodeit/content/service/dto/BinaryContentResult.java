package com.sprint.mission.discodeit.content.service.dto;

import com.sprint.mission.discodeit.content.entity.BinaryContent;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record BinaryContentResult(
        UUID id,
        Instant createdAt,
        String fileName,
        long size,
        String contentType,
        byte[] bytes
) {
    public BinaryContentResult {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public static BinaryContentResult from(BinaryContent content) {
        return new BinaryContentResult(
                content.getId(),
                content.getCreatedAt(),
                content.getFileName(),
                content.getSize(),
                content.getContentType(),
                content.getBytes()
        );
    }
}
