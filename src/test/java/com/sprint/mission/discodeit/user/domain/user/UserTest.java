package com.sprint.mission.discodeit.user.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void constructorRejectsInvalidEmailFormat() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new User("username", "invalid-email", "password", null)
        );
    }

    @Test
    void failedEmailUpdateDoesNotChangeOtherFields() {
        User user = new User("before", "before@example.com", "password", null);

        assertThrows(
                IllegalArgumentException.class,
                () -> user.update("after", "invalid-email", null, null)
        );

        assertEquals("before", user.getUsername());
        assertEquals("before@example.com", user.getEmail());
    }
}
