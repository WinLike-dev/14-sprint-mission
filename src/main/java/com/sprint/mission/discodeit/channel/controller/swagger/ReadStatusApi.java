package com.sprint.mission.discodeit.channel.controller.swagger;

import com.sprint.mission.discodeit.channel.controller.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.response.ReadStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ReadStatusDto.class))
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 읽음 상태가 존재함",
                    content = @Content
            )
    })
    ResponseEntity<ReadStatusDto> create(ReadStatusCreateRequest request);

    @Operation(summary = "User의 Message 읽음 상태 목록 조회")
    @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공")
    ResponseEntity<List<ReadStatusDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") UUID userId
    );

    @Operation(summary = "Message 읽음 상태 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태가 성공적으로 수정됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 올바르지 않음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<ReadStatusDto> updateLastReadAt(
            @Parameter(description = "수정할 읽음 상태 ID") UUID readStatusId,
            ReadStatusUpdateRequest request
    );
}
