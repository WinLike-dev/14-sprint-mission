package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * 읽음 상태 수정 요청 DTO.
 * 갱신할 수 있는 값이 마지막 읽은 시각 하나뿐이므로 필수다.
 */
public record ReadStatusUpdateRequest(
        @NotNull(message = "newLastReadAt은 필수입니다.")
        Instant newLastReadAt
) {
}
