package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 메시지 수정 요청 DTO.
 * 본문만 수정할 수 있고 전달값을 그대로 반영하므로 필수 값이다.
 */
public record MessageUpdateRequest(
        @NotBlank(message = "newContent는 필수입니다.")
        @Size(max = 2000, message = "newContent는 2000자를 넘을 수 없습니다.")
        String newContent
) {
}
