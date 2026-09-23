package com.sprint.mission.discodeit.content.controller.swagger;

import com.sprint.mission.discodeit.content.controller.dto.response.BinaryContentDto;
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

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<BinaryContentDto> find(
            @Parameter(description = "조회할 첨부 파일 ID") UUID binaryContentId
    );

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @Parameter(description = "조회할 첨부 파일 ID 목록") List<UUID> binaryContentIds
    );

    @Operation(summary = "파일 다운로드", operationId = "download")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "파일 다운로드 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<?> download(
            @Parameter(description = "다운로드할 파일 ID") UUID binaryContentId
    );
}
