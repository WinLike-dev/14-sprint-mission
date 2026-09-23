package com.sprint.mission.discodeit.channel.service.dto.result;

import com.sprint.mission.discodeit.channel.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResult(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {

    public static ReadStatusResult from(ReadStatus status) {
        return new ReadStatusResult(
                status.getId(),
                // 지연 로딩 프록시의 id는 초기화 없이 읽을 수 있어 추가 쿼리가 나가지 않는다.
                status.getUser().getId(),
                status.getChannel().getId(),
                status.getLastReadAt()
        );
    }
}
