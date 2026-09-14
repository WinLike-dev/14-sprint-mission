package com.sprint.mission.discodeit.content.service;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;

import java.util.List;
import java.util.UUID;

/**
 * REST 조회용 유스케이스 계약.
 * 생성/삭제는 REST로 열지 않고, 프로필·첨부를 다루는 서비스가 BinaryContentRepository로 직접 처리한다.
 */
public interface ContentControllerService {

    // ID로 단일 바이너리 콘텐츠를 조회한다
    BinaryContentResult find(UUID id);

    // 여러 ID로 바이너리 콘텐츠를 한 번에 조회한다
    List<BinaryContentResult> findAllByIdIn(List<UUID> ids);
}
