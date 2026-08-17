package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 생성 요청 DTO.
 * 생성이므로 모든 값이 필요하다. 여기서 걸러진 실패는 필드 단위로 응답에 담긴다.
 */
public record UserCreateRequest(
        @NotBlank(message = "username은 필수입니다.")
        @Size(max = 50, message = "username은 50자를 넘을 수 없습니다.")
        String username,

        @NotBlank(message = "email은 필수입니다.")
        @Email(message = "email 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "password는 필수입니다.")
        @Size(min = 4, max = 100, message = "password는 4자 이상 100자 이하여야 합니다.")
        String password
) {
}
