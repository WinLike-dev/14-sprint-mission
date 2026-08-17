package com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO.
 * 값이 비면 인증 실패(401)가 아니라 요청 오류(400)로 다뤄야 하므로 여기서 먼저 거른다.
 */
public record LoginRequest(
        @NotBlank(message = "username은 필수입니다.")
        String username,

        @NotBlank(message = "password는 필수입니다.")
        String password
) {
}
