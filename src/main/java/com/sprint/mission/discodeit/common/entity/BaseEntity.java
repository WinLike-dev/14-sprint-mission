package com.sprint.mission.discodeit.common.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Identifiable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    protected BaseEntity() {
        this(UUID.randomUUID(), Instant.now(), null);
    }

    protected BaseEntity(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id는 null일 수 없습니다.");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt은 null일 수 없습니다.");
        this.updatedAt = updatedAt;
    }

    protected final void markUpdated() {
        updatedAt = Instant.now();
    }
}
