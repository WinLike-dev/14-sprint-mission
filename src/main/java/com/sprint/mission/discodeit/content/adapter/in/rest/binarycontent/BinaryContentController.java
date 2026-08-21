package com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent;

import com.sprint.mission.discodeit.content.application.binarycontent.ContentControllerService;
import com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 REST inbound 어댑터.
 * 조회만 HTTP로 열고, 생성/삭제는 content 모듈의 노출 API를 통해서만 받는다.
 * 엔드포인트: /api/binaryContents
 */
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final ContentControllerService contentService;

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content
            )
    })
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId
    ) {
        return ResponseEntity.ok(contentService.find(binaryContentId));
    }

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @Parameter(description = "조회할 첨부 파일 ID 목록")
            @RequestParam List<UUID> binaryContentIds
    ) {
        return ResponseEntity.ok(contentService.findAllByIdIn(binaryContentIds));
    }
}
