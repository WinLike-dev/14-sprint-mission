package com.sprint.mission.discodeit.readstatus.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends CrudRepository<ReadStatus> {

    default List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    default List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    default Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .filter(status -> status.getChannelId().equals(channelId))
                .findFirst();
    }

    default void deleteAllByUserId(UUID userId) {
        findAllByUserId(userId).forEach(status -> deleteById(status.getId()));
    }

    default void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(status -> deleteById(status.getId()));
    }
}
