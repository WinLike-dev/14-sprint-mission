package com.sprint.mission.discodeit.user.adapter.out.persistence.status.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;

import java.nio.file.Path;

// 파생 조회는 UserStatusRepository의 default 구현을 그대로 쓴다.
public final class FileUserStatusRepository extends AbstractFileRepository<UserStatus>
        implements UserStatusRepository {

    public FileUserStatusRepository(Path root) {
        super(root.resolve("user-statuses"), UserStatus.class);
    }
}
