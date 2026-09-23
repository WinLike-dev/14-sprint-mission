package com.sprint.mission.discodeit.message.controller.swagger;

import com.sprint.mission.discodeit.message.controller.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.controller.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import com.sprint.mission.discodeit.message.controller.dto.response.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message가 성공적으로 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = MessageDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<MessageDto> create(
            MessageCreateRequest request,
            List<MultipartFile> attachments
    );

    @Operation(summary = "Channel의 Message 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공")
    ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            @Parameter(description = "조회할 Channel ID") UUID channelId,
            @Parameter(description = "페이지 정보. 기본값은 최근 순 50개") Pageable pageable
    );

    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨"),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<MessageDto> update(
            @Parameter(description = "수정할 Message ID") UUID messageId,
            MessageUpdateRequest request
    );

    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Message가 성공적으로 삭제됨",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message를 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Message ID") UUID messageId
    );
}
