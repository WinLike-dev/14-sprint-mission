package com.sprint.mission.discodeit.user.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;

import java.nio.file.Path;

public final class FileUserRepository extends AbstractFileRepository<User>
        implements UserRepository {

    public FileUserRepository(Path root) {
        super(root.resolve("users"), User.class);
    }
}
