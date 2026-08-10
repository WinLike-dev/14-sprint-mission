package com.sprint.mission.discodeit.userstatus.entity;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    void updateLastActiveAtUsesCurrentTime() {
        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                Instant.parse("2026-08-09T00:00:00Z")
        );
        Instant beforeUpdate = Instant.now();

        status.updateLastActiveAt();

        Instant afterUpdate = Instant.now();
        assertFalse(status.getLastActiveAt().isBefore(beforeUpdate));
        assertFalse(status.getLastActiveAt().isAfter(afterUpdate));
        assertNotNull(status.getUpdatedAt());
    }
}
