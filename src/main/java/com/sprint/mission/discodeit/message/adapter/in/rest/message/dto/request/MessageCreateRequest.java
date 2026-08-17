package com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * 메시지 생성 요청 DTO.
 * 첨부는 선택 항목이라 없으면 빈 목록으로 본다.
 * 첨부가 있으면 @Valid로 각 항목까지 함께 검증한다.
 */
public record MessageCreateRequest(
        @NotBlank(message = "content는 필수입니다.")
        @Size(max = 2000, message = "content는 2000자를 넘을 수 없습니다.")
        String content,

        @NotNull(message = "channelId는 필수입니다.")
        UUID channelId,

        @NotNull(message = "authorId는 필수입니다.")
        UUID authorId,

        @Valid
        List<@Valid MessageAttachmentCreateRequest> attachments
) {
    public MessageCreateRequest {
        // 첨부 없음은 정상이므로 빈 목록으로 정규화한다.
        attachments = attachments == null ? List.of() : List.copyOf(attachments);
    }
}
