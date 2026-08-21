package com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 채널 수정 요청 DTO.
 * 부분 수정이므로 null은 "변경하지 않음"을 뜻한다. 값이 들어온 경우에만 형식을 검사한다.
 *
 * @param newName        변경할 채널 이름 (null이면 기존 값 유지)
 * @param newDescription 변경할 채널 설명 (null이면 기존 값 유지)
 */
public record PublicChannelUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "newName은 공백일 수 없습니다.")
        @Size(max = 100, message = "newName은 100자를 넘을 수 없습니다.")
        String newName,

        @Size(max = 500, message = "newDescription은 500자를 넘을 수 없습니다.")
        String newDescription
) {
}
