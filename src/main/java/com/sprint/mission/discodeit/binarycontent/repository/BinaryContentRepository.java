package com.sprint.mission.discodeit.binarycontent.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

// Crud는 기본 CRUD를 다루고 이 인터페이스는 바이너리컨텐트 만의 계약
public interface BinaryContentRepository extends CrudRepository<BinaryContent> {

    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}
