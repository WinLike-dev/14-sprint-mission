package com.sprint.mission.discodeit.user.adapter.out.persistence.user.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;

import java.nio.file.Path;

// 파생 조회는 UserRepository의 default 구현을 그대로 쓴다.
public final class FileUserRepository extends AbstractFileRepository<User>
        implements UserRepository {

    public FileUserRepository(Path root) {
        super(root.resolve("users"), User.class);
    }
}
