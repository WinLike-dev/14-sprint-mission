package com.sprint.mission.discodeit.content.api;

import java.util.Arrays;
import java.util.Objects;

/**
 * ContentInternalApi 생성 요청에 실어 보내는 데이터.
 * 다른 모듈의 outbound ACL이 이 레코드로 변환해 호출한다.
 * 모든 필드에 null 체크와 방어적 복사를 적용한다.
 */
public record BinaryContentPayload(
        String fileName,     // 파일 이름
        String contentType,  // MIME 타입
        byte[] bytes         // 파일의 실제 바이너리 데이터
) {

    // 컴팩트 생성자: null 체크 및 byte 배열 방어적 복사
    public BinaryContentPayload {
        Objects.requireNonNull(fileName, "fileName은 null일 수 없습니다.");
        Objects.requireNonNull(contentType, "contentType은 null일 수 없습니다.");
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // getter에서도 방어적 복사를 반환하여 외부에서 내부 배열을 수정할 수 없게 한다
    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
