package com.sprint.mission.discodeit.user.domain.status;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        assertEquals(newActiveAt, status.getLastActiveAt());
        assertNotNull(status.getUpdatedAt());
        assertThrows(NullPointerException.class, () -> status.updateLastActiveAt(null));
    }
}
