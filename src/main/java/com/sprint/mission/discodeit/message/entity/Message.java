package com.sprint.mission.discodeit.message.entity;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String content;
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.content = requireNonBlank(content);
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.authorId = Objects.requireNonNull(authorId, "authorId는 null일 수 없습니다.");
        this.attachmentIds = List.copyOf(Objects.requireNonNull(attachmentIds));
    }

    private Message(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) {
        super(id, createdAt, updatedAt);
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = List.copyOf(attachmentIds);
    }

    public void update(String content) {
        this.content = requireNonBlank(content);
        markUpdated();
    }

    // 카피 오퍼레이터로 객체 그대로 복사 목표는 JCF의 저장소와 서비스 데이터 격리 둘 다 메모리 안이라 격리해야함
    public Message copy() {
        return new Message(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                content,
                channelId,
                authorId,
                attachmentIds
        );
    }

    private static String requireNonBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("content은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }
}
