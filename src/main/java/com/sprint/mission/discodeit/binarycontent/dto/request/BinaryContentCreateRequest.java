package com.sprint.mission.discodeit.binarycontent.dto.request;

import java.util.Arrays;
import java.util.Objects;

public record BinaryContentCreateRequest(String fileName, String contentType, byte[] bytes) {

    // record의 컴팩트 생성자
    public BinaryContentCreateRequest {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }
}
