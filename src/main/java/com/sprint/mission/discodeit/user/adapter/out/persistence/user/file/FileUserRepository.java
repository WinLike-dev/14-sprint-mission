package com.sprint.mission.discodeit.user.adapter.out.persistence.user.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;

import java.nio.file.Path;
import java.util.Optional;

public final class FileUserRepository extends AbstractFileRepository<User>
        implements UserRepository {

    public FileUserRepository(Path root) {
        super(root.resolve("users"), User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // 문법: stream과 람다는 저장된 사용자 중 이름이 일치하는 첫 객체를 찾는다.
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
