package com.sprint.mission.discodeit.userstatus.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends CrudRepository<UserStatus> {

    Optional<UserStatus> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
