package com.sprint.mission.discodeit.user.entity;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserStatusTest {

    @Test
    void exactlyFiveMinutesAgoIsOnline() {
        Instant now = Instant.parse("2026-08-09T00:00:00Z");
        UserStatus status = statusOfNewUser(now.minus(Duration.ofMinutes(5)));

        assertTrue(status.isOnline(now));
    }

    @Test
    void moreThanFiveMinutesAgoIsOffline() {
        Instant now = Instant.parse("2026-08-09T00:00:00Z");
        UserStatus status = statusOfNewUser(now.minus(Duration.ofMinutes(5)).minusNanos(1));

        assertFalse(status.isOnline(now));
    }

    // 활동 시각도 호출자가 알려준 값을 그대로 둔다.
    @Test
    void updateLastActiveAtKeepsGivenTime() {
        UserStatus status = statusOfNewUser(Instant.parse("2026-08-09T00:00:00Z"));
        Instant newActiveAt = Instant.parse("2026-08-09T00:03:00Z");

        status.updateLastActiveAt(newActiveAt);

        // updatedAt은 저장 시점에 JPA 콜백이 채우므로 도메인 단위 테스트에서는 확인하지 않는다.
        assertEquals(newActiveAt, status.getLastActiveAt());
        assertThrows(NullPointerException.class, () -> status.updateLastActiveAt(null));
    }

    // 사용자를 만들면 상태가 함께 만들어지고, 양쪽 참조가 서로를 가리킨다.
    @Test
    void newUserHasLinkedStatus() {
        Instant lastActiveAt = Instant.parse("2026-08-09T00:00:00Z");
        User user = new User("user", "user@example.com", "password", null, lastActiveAt);

        assertSame(user, user.getStatus().getUser());
        assertEquals(lastActiveAt, user.getStatus().getLastActiveAt());
    }

    // 상태는 User 생성자에서만 만들어지므로 테스트도 사용자를 거쳐 얻는다.
    private static UserStatus statusOfNewUser(Instant lastActiveAt) {
        return new User("user", "user@example.com", "password", null, lastActiveAt).getStatus();
    }
}
