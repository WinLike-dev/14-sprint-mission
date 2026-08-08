package com.sprint.mission.discodeit.binarycontent.entity;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class BinaryContent implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final long size;
    private final String contentType;
    @Getter(lombok.AccessLevel.NONE)
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this(UUID.randomUUID(), Instant.now(), fileName, contentType, bytes);
    }

    private BinaryContent(
            UUID id,
            Instant createdAt,
            String fileName,
            String contentType,
            byte[] bytes
    ) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt은 null일 수 없습니다.");
        this.fileName = requireNonBlank(fileName, "fileName");
        this.contentType = requireNonBlank(contentType, "contentType");
        this.bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
        this.size = this.bytes.length;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public BinaryContent copy() {
        return new BinaryContent(id, createdAt, fileName, contentType, bytes);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
