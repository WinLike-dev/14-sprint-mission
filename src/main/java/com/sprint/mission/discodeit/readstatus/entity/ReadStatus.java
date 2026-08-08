package com.sprint.mission.discodeit.readstatus.entity;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");
    }

    private ReadStatus(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant lastReadAt) {
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");
        markUpdated();
    }

    public ReadStatus copy() {
        return new ReadStatus(
                getId(), getCreatedAt(), getUpdatedAt(), userId, channelId, lastReadAt
        );
    }
}
