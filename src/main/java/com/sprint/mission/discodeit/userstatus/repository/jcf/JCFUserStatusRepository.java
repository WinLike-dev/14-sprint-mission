package com.sprint.mission.discodeit.userstatus.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;

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
