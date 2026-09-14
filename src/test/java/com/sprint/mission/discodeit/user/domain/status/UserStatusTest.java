package com.sprint.mission.discodeit.user.domain.status;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserStatusTest {

    @Test
    void exactlyFiveMinutesAgoIsOnline() {
        Instant now = Instant.parse("2026-08-09T00:00:00Z");
        UserStatus status = new UserStatus(
                UUID.randomUUID(), now.minus(Duration.ofMinutes(5))
        );

        assertTrue(status.isOnline(now));
    }

    @Test
    void moreThanFiveMinutesAgoIsOffline() {
        Instant now = Instant.parse("2026-08-09T00:00:00Z");
        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                now.minus(Duration.ofMinutes(5)).minusNanos(1)
        );

        assertFalse(status.isOnline(now));
    }

    // 활동 시각도 호출자가 알려준 값을 그대로 둔다.
    @Test
    void updateLastActiveAtKeepsGivenTime() {
        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                Instant.parse("2026-08-09T00:00:00Z")
        );
        Instant newActiveAt = Instant.parse("2026-08-09T00:03:00Z");

        status.updateLastActiveAt(newActiveAt);

        // updatedAt은 저장 시점에 JPA 콜백이 채우므로 도메인 단위 테스트에서는 확인하지 않는다.
        assertEquals(newActiveAt, status.getLastActiveAt());
        assertThrows(NullPointerException.class, () -> status.updateLastActiveAt(null));
    }
}
