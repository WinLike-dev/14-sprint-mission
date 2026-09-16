package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * 사용자의 온라인 상태를 나타내는 도메인 엔티티.
 * 마지막 활동 시각(lastActiveAt)을 기록하고,
 * 현재 시각과 비교하여 사용자가 온라인인지 판단한다.
 * User와 양방향 1:1 관계이며, FK(user_id)를 가진 연관관계의 주인이다.
 * 사용자는 항상 상태를 가지므로 User가 생성될 때 함께 만들어진다.
 */
@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class UserStatus extends BaseUpdatableEntity {

    // 마지막 활동으로부터 이 시간(5분) 이내이면 "온라인"으로 간주한다.
    private static final Duration ONLINE_WINDOW = Duration.ofMinutes(5);

    // FK(user_id)를 가진 연관관계의 주인. 주인 쪽이라 LAZY가 실제로 동작한다.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true, updatable = false)
    private User user;               // 이 상태가 속하는 사용자

    @Column(nullable = false)
    private Instant lastActiveAt;    // 사용자가 마지막으로 활동한 시각

    // User 생성자만 호출한다. 같은 패키지에서만 보이도록 package-private으로 두어
    // 상태가 사용자와 따로 만들어지거나 다른 사용자에게 붙는 일을 막는다.
    UserStatus(User user, Instant lastActiveAt) {
        this.user = Objects.requireNonNull(user, "user는 null일 수 없습니다.");
        this.lastActiveAt = Objects.requireNonNull(lastActiveAt, "lastActiveAt은 null일 수 없습니다.");
    }

    // 마지막 활동 시각을 갱신한다. 로그인이나 API 호출 시 사용.
    // 활동 시각은 호출자가 알려준다. 서버 시각으로 덮어쓰지 않는다.
    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = Objects.requireNonNull(newLastActiveAt, "newLastActiveAt은 null일 수 없습니다.");
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
}
