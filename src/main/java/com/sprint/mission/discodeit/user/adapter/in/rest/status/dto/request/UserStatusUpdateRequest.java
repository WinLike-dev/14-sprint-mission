package com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * 사용자 접속 상태 수정 요청 DTO.
 * 갱신할 수 있는 값이 마지막 활동 시각 하나뿐이므로 필수다.
 */
public record UserStatusUpdateRequest(
        @NotNull(message = "newLastActiveAt은 필수입니다.")
        Instant newLastActiveAt
) {
}
