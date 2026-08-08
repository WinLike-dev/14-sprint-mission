package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import com.sprint.mission.discodeit.channel.exception.UnsupportedChannelOperationException;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ChannelType type;
    private String name;
    private String description;

    private Channel(ChannelType type, String name, String description) {
        this.type = Objects.requireNonNull(type, "type은 null일 수 없습니다.");
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
    }

    private Channel(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description
    ) {
        super(id, createdAt, updatedAt);
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public static Channel publicChannel(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description);
    }

    public static Channel privateChannel() {
        return new Channel(ChannelType.PRIVATE, null, null);
    }

    // PRIVATE는 DM이므로 이름이랑 설명이 없다. 따라서 지원되지 않는 예외 처리
    public void update(String name, String description) {
        if (type == ChannelType.PRIVATE) {
            throw new UnsupportedChannelOperationException(getId(), "update");
        }
        validateFields(type, name, description);
        this.name = name;
        this.description = description;
        markUpdated();
    }

    // JCF 객체 카피를 위한 오퍼레이터
    public Channel copy() {
        return new Channel(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                type,
                name,
                description
        );
    }

    // PRIVATE는 DM이므로 이름과 채널 설명 필요 없음
    private static void validateFields(ChannelType type, String name, String description) {
        if (type == ChannelType.PRIVATE) {
            if (name != null || description != null) {
                throw new IllegalArgumentException("PRIVATE 채널은 name과 description을 가질 수 없습니다.");
            }
            return;
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("PUBLIC 채널의 name은 비어 있을 수 없습니다.");
        }
        Objects.requireNonNull(description, "PUBLIC 채널의 description은 null일 수 없습니다.");
    }
}
