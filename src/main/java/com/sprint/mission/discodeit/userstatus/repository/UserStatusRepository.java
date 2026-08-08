package com.sprint.mission.discodeit.userstatus.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends CrudRepository<UserStatus> {

    default Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    default void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(status -> deleteById(status.getId()));
    }
}
