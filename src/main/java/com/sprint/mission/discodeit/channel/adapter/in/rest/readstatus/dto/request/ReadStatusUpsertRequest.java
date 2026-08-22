package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * 읽음 상태 등록 요청 DTO.
 *
 * 대상은 경로의 (userId, channelId)가 지정하므로 본문에는 담을 상태만 싣는다.
 *
 * 읽은 시각은 클라이언트가 정한다. 서버가 수신 시각으로 대신 정하면
 * 네트워크 지연만큼 실제로 읽은 시점과 어긋난다.
 */
public record ReadStatusUpsertRequest(
        @NotNull(message = "lastReadAt은 필수입니다.")
        Instant lastReadAt
) {
}
