package com.sprint.mission.discodeit.user.adapter.out.persistence.status.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;

import java.util.Optional;
import java.util.UUID;

public final class JCFUserStatusRepository extends AbstractJCFRepository<UserStatus>
        implements UserStatusRepository {

    public JCFUserStatusRepository() {
        super(UserStatus.class, UserStatus::copy);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(status -> deleteById(status.getId()));
    }
}
