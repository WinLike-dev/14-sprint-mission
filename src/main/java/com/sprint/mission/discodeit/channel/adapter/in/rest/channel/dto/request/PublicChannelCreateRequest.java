package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 공개 채널 생성 요청 DTO.
 * PUBLIC 채널은 이름과 설명을 반드시 갖는다는 도메인 규칙을 경계에서 먼저 확인한다.
 */
public record PublicChannelCreateRequest(
        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 100, message = "name은 100자를 넘을 수 없습니다.")
        String name,

        @NotNull(message = "description은 필수입니다.")
        @Size(max = 500, message = "description은 500자를 넘을 수 없습니다.")
        String description
) {
}
