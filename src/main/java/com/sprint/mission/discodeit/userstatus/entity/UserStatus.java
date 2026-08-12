package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Duration ONLINE_WINDOW = Duration.ofMinutes(5);

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.lastActiveAt = Objects.requireNonNull(lastActiveAt, "lastActiveAt은 null일 수 없습니다.");
    }

    private UserStatus(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            Instant lastActiveAt
    ) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    // 설계: 마지막 활동 시각은 외부 입력으로 교체하지 않고 UserStatus가 현재 시각으로 갱신한다.
    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        markUpdated();
    }

    public boolean isOnline() {
        return isOnline(Instant.now());
    }

    public boolean isOnline(Instant now) {
        // threshold는 현재 시각에 5분 빼기
        Instant threshold = Objects.requireNonNull(now).minus(ONLINE_WINDOW);
        // 정확히 5분 전 인지 체크
        return !lastActiveAt.isBefore(threshold);
    }

    public UserStatus copy() {
        return new UserStatus(
                getId(), getCreatedAt(), getUpdatedAt(), userId, lastActiveAt
        );
    }
}
