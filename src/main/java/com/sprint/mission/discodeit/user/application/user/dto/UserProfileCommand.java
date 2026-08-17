package com.sprint.mission.discodeit.user.application.user.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * 사용자 생성/수정 유스케이스가 받는 프로필 이미지 입력.
 * HTTP multipart/쿼리 형태와 무관하게, 파일 내용만 애플리케이션으로 넘긴다.
 */
public record UserProfileCommand(String fileName, String contentType, byte[] bytes) {

    public UserProfileCommand {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
