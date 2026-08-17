package com.sprint.mission.discodeit.user.application.port.out;

import java.util.Arrays;
import java.util.Objects;

/**
 * UserContentManager outbound 포트가 주고받는 프로필 이미지 데이터.
 * ACL이 이 값을 content 모듈의 BinaryContentPayload로 변환한다.
 */
public record UserContentData(String fileName, String contentType, byte[] bytes) {

    // 컴팩트 생성자: 바이트 배열을 방어적으로 복사하여 외부 변경으로부터 보호한다.
    public UserContentData {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // 바이트 배열 접근 시에도 복사본을 반환하여 내부 데이터를 보호한다.
    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
