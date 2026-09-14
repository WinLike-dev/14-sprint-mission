package com.sprint.mission.discodeit.content.controller;

import com.sprint.mission.discodeit.content.mapper.BinaryContentRestMapper;
import com.sprint.mission.discodeit.content.service.ContentControllerService;
import com.sprint.mission.discodeit.content.dto.response.BinaryContentDto;
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
 * 바이너리 콘텐츠 REST 컨트롤러.
 * 조회만 HTTP로 연다. 생성/삭제는 프로필·첨부를 다루는 서비스가 처리한다.
 * 엔드포인트: /api/binaryContents
 */
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

    private final ContentControllerService contentService;
    private final BinaryContentRestMapper contentMapper;

    @Override
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable UUID binaryContentId
    ) {
        return ResponseEntity.ok(
                contentMapper.toResponse(contentService.find(binaryContentId))
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        return ResponseEntity.ok(
                contentMapper.toResponses(contentService.findAllByIdIn(binaryContentIds))
        );
    }
}
