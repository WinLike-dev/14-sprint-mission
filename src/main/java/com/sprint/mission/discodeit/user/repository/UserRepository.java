package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User> {

    default Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    default boolean existsByUsername(String username) {
        return findAll().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    default boolean existsByEmail(String email) {
        return findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
