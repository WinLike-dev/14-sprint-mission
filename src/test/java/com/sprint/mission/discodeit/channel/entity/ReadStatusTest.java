package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.user.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReadStatusTest {

    // 읽은 시각은 클라이언트가 정한다. 서버 수신 시각으로 대신 정하면
    // 네트워크 지연만큼 실제로 읽은 시점과 어긋난다.
    @Test
    void creationAndUpdateKeepGivenTime() {
        Instant readAt = Instant.parse("2026-08-09T00:00:00Z");
        ReadStatus status = new ReadStatus(newUser(), newChannel(), readAt);

        assertEquals(readAt, status.getLastReadAt());

        Instant newReadAt = Instant.parse("2026-08-09T01:23:45Z");
        status.updateLastReadAt(newReadAt);

        // updatedAt은 저장 시점에 JPA 콜백이 채우므로 도메인 단위 테스트에서는 확인하지 않는다.
        assertEquals(newReadAt, status.getLastReadAt());
    }

    @Test
    void timeIsRequired() {
        User user = newUser();
        Channel channel = newChannel();

        assertThrows(
                NullPointerException.class,
                () -> new ReadStatus(user, channel, null)
        );

        ReadStatus status = new ReadStatus(user, channel, Instant.now());
        assertThrows(NullPointerException.class, () -> status.updateLastReadAt(null));
    }

    private static User newUser() {
        return new User("user", "user@example.com", "password", null, Instant.now());
    }

    private static Channel newChannel() {
        return Channel.publicChannel("general", "general channel");
    }
}
