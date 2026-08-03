package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Channel implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private final ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                null,
                type,
                name,
                description
        );
    }

    public void update(String name, String description) {
        String validatedName = requireNonBlank(name, "name");
        String validatedDescription = Objects.requireNonNull(
                description,
                "description은 null일 수 없습니다."
        );

        this.name = validatedName;
        this.description = validatedDescription;
        this.updatedAt = System.currentTimeMillis();
    }

    public Channel copy() {
        return new Channel(
                id,
                createdAt,
                updatedAt,
                type,
                name,
                description
        );
    }

    private Channel(
            UUID id,
            Long createdAt,
            Long updatedAt,
            ChannelType type,
            String name,
            String description
    ) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(
                createdAt,
                "createdAt은 null일 수 없습니다."
        );
        this.updatedAt = updatedAt;
        this.type = Objects.requireNonNull(
                type,
                "type은 null일 수 없습니다."
        );
        this.name = requireNonBlank(name, "name");
        this.description = Objects.requireNonNull(
                description,
                "description은 null일 수 없습니다."
        );
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 비어 있을 수 없습니다."
            );
        }
        return value;
    }
}
