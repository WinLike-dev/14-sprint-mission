package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Arrays;

/**
 * 메시지 첨부파일 생성 요청 DTO.
 *
 * 값이 없을 때 생성자에서 바로 터뜨리지 않는다.
 * 역직렬화 단계에서 NPE가 나면 어떤 필드가 빠졌는지 알려주지 못한 채 400이 나가기 때문에,
 * 누락 판단은 Bean Validation에 맡기고 여기서는 방어적 복사만 한다.
 */
public record MessageAttachmentCreateRequest(
        @NotBlank(message = "fileName은 필수입니다.")
        String fileName,    // 파일 이름 (예: "photo.png")

        @NotBlank(message = "contentType은 필수입니다.")
        String contentType,  // MIME 타입 (예: "image/png")

        @NotEmpty(message = "bytes는 비어 있을 수 없습니다.")
        byte[] bytes         // 파일의 실제 바이너리 데이터
) {
    public MessageAttachmentCreateRequest {
        bytes = bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public byte[] bytes() {
        return bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
    }
}
