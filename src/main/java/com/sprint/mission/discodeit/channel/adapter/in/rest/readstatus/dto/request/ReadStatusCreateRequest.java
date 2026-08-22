package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * 읽음 상태 생성 요청 DTO.
 * 읽은 시각은 클라이언트가 정한다. 서버가 수신 시각으로 대신 정하면
 * 네트워크 지연만큼 실제로 읽은 시점과 어긋난다.
 */
public record ReadStatusCreateRequest(
        @NotNull(message = "userId는 필수입니다.")
        UUID userId,

        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,

        @NotNull(message = "lastReadAt은 필수입니다.")
        Instant lastReadAt
) {
}
