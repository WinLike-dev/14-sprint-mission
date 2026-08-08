package com.sprint.mission.discodeit.binarycontent.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository extends CrudRepository<BinaryContent> {

    default List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream().map(this::findById).toList();
    }
}
