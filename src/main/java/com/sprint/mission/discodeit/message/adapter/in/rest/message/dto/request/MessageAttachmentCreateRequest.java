package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import java.util.Arrays;
import java.util.Objects;

/**
 * 메시지 첨부파일 생성 요청 DTO.
 * 메시지에 첨부할 파일 하나의 정보를 담는다.
 * byte 배열을 방어적 복사하여 데이터 무결성을 보장한다.
 */
public record MessageAttachmentCreateRequest(
        String fileName,    // 파일 이름 (예: "photo.png")
        String contentType,  // MIME 타입 (예: "image/png")
        byte[] bytes         // 파일의 실제 바이너리 데이터
) {

    // 컴팩트 생성자: byte 배열을 방어적으로 복사한다
    public MessageAttachmentCreateRequest {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // getter에서도 방어적 복사를 반환하여 외부에서 내부 배열을 수정할 수 없게 한다
    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
