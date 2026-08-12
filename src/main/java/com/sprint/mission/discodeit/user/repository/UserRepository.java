package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.user.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
