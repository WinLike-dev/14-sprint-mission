package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * 메시지 생성 요청 DTO.
 * multipart 요청의 messageCreateRequest 파트(application/json)로 들어온다.
 * 첨부파일은 attachments 파트로 따로 오므로 여기에 담지 않는다.
 */
public record MessageCreateRequest(
        @NotBlank(message = "content는 필수입니다.")
        @Size(max = 2000, message = "content는 2000자를 넘을 수 없습니다.")
        String content,

        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,

        @NotNull(message = "authorId는 필수입니다.")
        UUID authorId
) {
}
