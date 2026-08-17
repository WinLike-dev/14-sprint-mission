package com.sprint.mission.discodeit.content.application.binarycontent;

import com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response.BinaryContentDto;

import java.util.List;
import java.util.UUID;

/**
 * REST 조회용 유스케이스 계약.
 * 생성/삭제는 content 모듈의 노출 API(ContentInternalApi)로만 연다.
 */
public interface ContentControllerService {

    // ID로 단일 바이너리 콘텐츠를 조회한다
    BinaryContentDto find(UUID id);

    // 여러 ID로 바이너리 콘텐츠를 한 번에 조회한다
    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);
}
