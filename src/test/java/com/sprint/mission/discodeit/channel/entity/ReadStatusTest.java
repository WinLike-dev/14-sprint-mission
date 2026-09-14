package com.sprint.mission.discodeit.channel.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReadStatusTest {

    // 읽은 시각은 클라이언트가 정한다. 서버 수신 시각으로 대신 정하면
    // 네트워크 지연만큼 실제로 읽은 시점과 어긋난다.
    @Test
    void creationAndUpdateKeepGivenTime() {
        Instant readAt = Instant.parse("2026-08-09T00:00:00Z");
        ReadStatus status = new ReadStatus(UUID.randomUUID(), UUID.randomUUID(), readAt);

        assertEquals(readAt, status.getLastReadAt());

        Instant newReadAt = Instant.parse("2026-08-09T01:23:45Z");
        status.updateLastReadAt(newReadAt);

        // updatedAt은 저장 시점에 JPA 콜백이 채우므로 도메인 단위 테스트에서는 확인하지 않는다.
        assertEquals(newReadAt, status.getLastReadAt());
    }

    @Test
    void timeIsRequired() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        assertThrows(
                NullPointerException.class,
                () -> new ReadStatus(userId, channelId, null)
        );

        ReadStatus status = new ReadStatus(userId, channelId, Instant.now());
        assertThrows(NullPointerException.class, () -> status.updateLastReadAt(null));
    }
}
