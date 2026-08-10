package com.sprint.mission.discodeit.readstatus.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReadStatusTest {

    @Test
    void creationAndUpdateUseServerCurrentTime() {
        Instant beforeCreation = Instant.now();
        ReadStatus status = new ReadStatus(UUID.randomUUID(), UUID.randomUUID());
        Instant afterCreation = Instant.now();

        assertFalse(status.getLastReadAt().isBefore(beforeCreation));
        assertFalse(status.getLastReadAt().isAfter(afterCreation));

        Instant beforeUpdate = Instant.now();
        status.updateLastReadAt();
        Instant afterUpdate = Instant.now();

        assertFalse(status.getLastReadAt().isBefore(beforeUpdate));
        assertFalse(status.getLastReadAt().isAfter(afterUpdate));
        assertNotNull(status.getUpdatedAt());
        assertFalse(status.getUpdatedAt().isBefore(status.getLastReadAt()));
    }
}
