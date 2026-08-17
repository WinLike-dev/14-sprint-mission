package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;

/**
 * 프로필 이미지 생성 요청 DTO.
 *
 * 값이 없을 때 생성자에서 바로 터뜨리지 않는다.
 * 역직렬화 단계에서 NPE가 나면 어떤 필드가 빠졌는지 알려주지 못한 채 400이 나가기 때문에,
 * 누락 판단은 Bean Validation에 맡기고 여기서는 방어적 복사만 한다.
 */
public record UserProfileCreateRequest(
        @NotBlank(message = "fileName은 필수입니다.")
        String fileName,

        @NotBlank(message = "contentType은 필수입니다.")
        String contentType,

        @NotEmpty(message = "bytes는 비어 있을 수 없습니다.")
        byte[] bytes
) {
    public UserProfileCreateRequest {
        bytes = bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public byte[] bytes() {
        return bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }
}
