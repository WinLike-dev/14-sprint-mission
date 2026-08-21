package com.sprint.mission.discodeit.user.domain.status;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자의 온라인 상태를 나타내는 도메인 엔티티.
 * 마지막 활동 시각(lastActiveAt)을 기록하고,
 * 현재 시각과 비교하여 사용자가 온라인인지 판단한다.
 * User와 1:1 관계이며, userId로 연결된다.
 */
@Getter
public class UserStatus extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 마지막 활동으로부터 이 시간(5분) 이내이면 "온라인"으로 간주한다.
    private static final Duration ONLINE_WINDOW = Duration.ofMinutes(5);

    private final UUID userId;       // 이 상태가 속하는 사용자의 ID
    private Instant lastActiveAt;    // 사용자가 마지막으로 활동한 시각

    // 새 UserStatus를 생성하는 생성자
    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        this.lastActiveAt = Objects.requireNonNull(lastActiveAt, "lastActiveAt은 null일 수 없습니다.");
    }

    // DB/파일에서 복원할 때 사용하는 내부 생성자
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
    // 마지막 활동 시각을 "지금"으로 업데이트한다. 로그인이나 API 호출 시 사용.
    // 활동 시각은 호출자가 알려준다. 서버 시각으로 덮어쓰지 않는다.
    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = Objects.requireNonNull(newLastActiveAt, "newLastActiveAt은 null일 수 없습니다.");
        markUpdated();
    }

    // 현재 시각 기준으로 사용자가 온라인인지 확인한다.
    public boolean isOnline() {
        return isOnline(Instant.now());
    }

    // 특정 시각 기준으로 사용자가 온라인인지 확인한다. 테스트에서 유용하다.
    public boolean isOnline(Instant now) {
        // threshold는 현재 시각에 5분 빼기
        Instant threshold = Objects.requireNonNull(now).minus(ONLINE_WINDOW);
        // 정확히 5분 전 인지 체크
        return !lastActiveAt.isBefore(threshold); // lastActiveAt >= threshold 이면 온라인
    }

    // 깊은 복사본을 만든다. 원본 변경 없이 안전하게 다룰 때 사용한다.
    public UserStatus copy() {
        return new UserStatus(
                getId(), getCreatedAt(), getUpdatedAt(), userId, lastActiveAt
        );
    }
}
