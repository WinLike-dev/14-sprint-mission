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

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.channelId = Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        this.lastReadAt = Instant.now();
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

    // 설계: 읽은 시각은 외부 입력이 아니라 서버의 현재 시각으로 갱신한다.
    public void updateLastReadAt() {
        this.lastReadAt = Instant.now();
        markUpdated();
    }

    // 이것도 JCF를 위해
    public ReadStatus copy() {
        return new ReadStatus(
                getId(), getCreatedAt(), getUpdatedAt(), userId, channelId, lastReadAt
        );
    }
}
