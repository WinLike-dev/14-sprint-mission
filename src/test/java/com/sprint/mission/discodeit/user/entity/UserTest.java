package com.sprint.mission.discodeit.user.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void constructorRejectsInvalidEmailFormat() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User("username", "invalid-email", "password", null, Instant.now())
        );
    }

    @Test
    void failedEmailUpdateDoesNotChangeOtherFields() {
        User user = new User("before", "before@example.com", "password", null, Instant.now());

        assertThrows(
                IllegalArgumentException.class,
                () -> user.update("after", "invalid-email", null)
        );

        assertEquals("before", user.getUsername());
        assertEquals("before@example.com", user.getEmail());
    }

    @Test
    void constructorRejectsPasswordLongerThanColumn() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User("username", "user@example.com", "a".repeat(61), null, Instant.now())
        );
    }

    @Test
    void updateRejectsUsernameLongerThanColumn() {
        User user = new User("before", "before@example.com", "password", null, Instant.now());

        assertThrows(
                IllegalArgumentException.class,
                () -> user.update("a".repeat(51), "before@example.com", "password")
        );

        assertEquals("before", user.getUsername());
    }
}
