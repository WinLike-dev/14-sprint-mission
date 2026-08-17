package com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request;

import java.util.Arrays;
import java.util.Objects;

/**
 * 사용자 프로필 이미지 생성 요청 DTO.
 * 파일 이름, 콘텐츠 타입(MIME), 실제 바이트 데이터를 담는다.
 * 바이트 배열은 방어적 복사를 통해 외부 변경으로부터 보호한다.
 */
public record UserProfileCreateRequest(String fileName, String contentType, byte[] bytes) {

    // 컴팩트 생성자: 전달받은 바이트 배열을 복사하여 원본 변경이 내부에 영향을 주지 않도록 한다.
    public UserProfileCreateRequest {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    // getter에서도 복사본을 반환하여 내부 바이트 배열이 외부에서 수정되지 않도록 한다.
    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
