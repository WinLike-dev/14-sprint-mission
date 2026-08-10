package com.sprint.mission.discodeit.readstatus.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends CrudRepository<ReadStatus> {

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteAllByUserId(UUID userId);

    void deleteAllByChannelId(UUID channelId);
}
