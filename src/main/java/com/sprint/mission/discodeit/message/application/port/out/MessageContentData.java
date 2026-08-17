package com.sprint.mission.discodeit.message.application.port.out;

import java.util.Arrays;
import java.util.Objects;

/**
 * MessageContentManager outbound 포트가 주고받는 첨부파일 데이터.
 * ACL이 이 값을 content 모듈의 BinaryContentPayload로 변환한다.
 */
public record MessageContentData(String fileName, String contentType, byte[] bytes) {

    // 컴팩트 생성자: 생성 시 byte 배열을 방어적으로 복사한다
    public MessageContentData {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // getter에서도 방어적 복사를 반환하여 외부에서 내부 배열을 수정할 수 없게 한다
    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
