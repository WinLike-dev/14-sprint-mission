package com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent;

import com.sprint.mission.discodeit.content.application.binarycontent.ContentControllerService;
import com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response.BinaryContentDto;
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
public class BinaryContentController {

    private final ContentControllerService contentService;

    // GET /api/binaryContents/{binaryContentId} - 단일 바이너리 콘텐츠를 ID로 조회한다
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
        return ResponseEntity.ok(contentService.find(binaryContentId));
    }

    // GET /api/binaryContents?binaryContentIds=...&binaryContentIds=... - 여러 ID로 바이너리 콘텐츠를 한 번에 조회한다
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity.ok(contentService.findAllByIdIn(binaryContentIds));
    }
}
