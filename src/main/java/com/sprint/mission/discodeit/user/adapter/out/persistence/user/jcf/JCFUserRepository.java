package com.sprint.mission.discodeit.user.adapter.out.persistence.user.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;

import java.util.Optional;

public final class JCFUserRepository extends AbstractJCFRepository<User>
        implements UserRepository {

    public JCFUserRepository() {
        super(User.class, User::copy);
    }

    @Override
    public Optional<User> findByUsername(String username) {
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
