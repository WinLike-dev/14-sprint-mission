package com.sprint.mission.discodeit.content.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;

import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 저장소 outbound 포트.
 * 여러 ID로 한 번에 조회하는 계약을 추가로 정의한다.
 */
// Crud는 기본 CRUD를 다루고 이 인터페이스는 바이너리컨텐트 만의 계약
public interface BinaryContentRepository extends CrudRepository<BinaryContent> {

    // 여러 개의 ID에 해당하는 BinaryContent를 한 번에 조회한다
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}
