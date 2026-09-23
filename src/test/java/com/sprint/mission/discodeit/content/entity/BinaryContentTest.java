package com.sprint.mission.discodeit.content.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryContentTest {

    @Test
    void fileNameUpToColumnLengthIsAccepted() {
        assertDoesNotThrow(() -> new BinaryContent("a".repeat(255), 1, "image/png"));
    }

    @Test
    void fileNameLongerThanColumnIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BinaryContent("a".repeat(256), 1, "image/png")
        );
    }

    @Test
    void contentTypeLongerThanColumnIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BinaryContent("photo.png", 1, "a".repeat(101))
        );
    }
}
