package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String content;
    private final UUID channelId;
    private final UUID senderId;
    private final UUID receiverId;

    public Message(String content, UUID channelId, UUID senderId, UUID receiverId) {
        this(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                null,
                content,
                channelId,
                senderId,
                receiverId
        );
    }

    public void update(String content) {
        this.content = requireNonBlank(content, "content");
        this.updatedAt = System.currentTimeMillis();
    }

    public Message copy() {
        return new Message(
                id,
                createdAt,
                updatedAt,
                content,
                channelId,
                senderId,
                receiverId
        );
    }

    private Message(
            UUID id,
            Long createdAt,
            Long updatedAt,
            String content,
            UUID channelId,
            UUID senderId,
            UUID receiverId
    ) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(
                createdAt,
                "createdAt은 null일 수 없습니다."
        );
        this.updatedAt = updatedAt;
        this.content = requireNonBlank(content, "content");
        this.channelId = Objects.requireNonNull(
                channelId,
                "channelId는 null일 수 없습니다."
        );
        this.senderId = Objects.requireNonNull(
                senderId,
                "senderId는 null일 수 없습니다."
        );
        this.receiverId = Objects.requireNonNull(
                receiverId,
                "receiverId는 null일 수 없습니다."
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
