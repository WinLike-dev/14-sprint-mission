package com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent;

import com.sprint.mission.discodeit.content.application.binarycontent.ContentControllerService;
import com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response.BinaryContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 REST inbound 어댑터.
 * 조회만 HTTP로 열고, 생성/삭제는 content 모듈의 노출 API를 통해서만 받는다.
 * 엔드포인트: /api/binary-contents
 */
@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final ContentControllerService contentService;

    // GET /api/binary-contents/{id} - 단일 바이너리 콘텐츠를 ID로 조회한다
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID id) {
        return ResponseEntity.ok(contentService.find(id));
    }

    // GET /api/binary-contents?ids=...&ids=... - 여러 ID로 바이너리 콘텐츠를 한 번에 조회한다
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(contentService.findAllByIdIn(ids));
    }
}
