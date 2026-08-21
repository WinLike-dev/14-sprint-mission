package com.sprint.mission.discodeit.message.application.message.dto;

import java.util.Arrays;
import java.util.Objects;

/**
 * 메시지 생성 유스케이스가 받는 첨부파일 입력.
 * HTTP multipart 형태와 무관하게, 파일 내용만 애플리케이션으로 넘긴다.
 */
public record MessageAttachmentCommand(String fileName, String contentType, byte[] bytes) {

    public MessageAttachmentCommand {
        bytes = Arrays.copyOf(Objects.requireNonNull(bytes), bytes.length);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
