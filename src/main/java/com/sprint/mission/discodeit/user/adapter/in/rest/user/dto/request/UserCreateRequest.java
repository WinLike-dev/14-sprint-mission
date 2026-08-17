package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 생성 요청 DTO.
 * 생성이므로 username/email/password는 모두 필요하다.
 * 여기서 걸러진 실패는 필드 단위로 응답에 담긴다.
 *
 * 프로필은 선택 항목이라 본문에 함께 싣는다.
 * 첨부를 본문에 담는 방식은 MessageCreateRequest.attachments와 같다.
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
        String password,

        // 선택 항목. 값이 있으면 내부 필드까지 함께 검증한다.
        @Valid
        UserProfileCreateRequest profile
) {
}
