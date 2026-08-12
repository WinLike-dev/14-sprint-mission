package com.sprint.mission.discodeit.userstatus.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class FileUserStatusRepository extends AbstractFileRepository<UserStatus>
        implements UserStatusRepository {

    public FileUserStatusRepository(Path root) {
        super(root.resolve("user-statuses"), UserStatus.class);
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
