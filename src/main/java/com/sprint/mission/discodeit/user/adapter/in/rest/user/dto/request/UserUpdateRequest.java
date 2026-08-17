package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 사용자 수정 요청 DTO.
 * 부분 수정이므로 null은 "변경하지 않음"을 뜻한다.
 * 따라서 @NotBlank를 걸지 않는다. 값이 들어온 경우에만 형식을 검사한다.
 * (@Email, @Pattern, @Size는 모두 null을 통과시킨다)
 */
public record UserUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "username은 공백일 수 없습니다.")
        @Size(max = 50, message = "username은 50자를 넘을 수 없습니다.")
        String username,

        @Email(message = "email 형식이 올바르지 않습니다.")
        String email,

        @Pattern(regexp = ".*\\S.*", message = "password는 공백일 수 없습니다.")
        @Size(min = 4, max = 100, message = "password는 4자 이상 100자 이하여야 합니다.")
        String password
) {
}
