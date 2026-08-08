package com.sprint.mission.discodeit.userstatus.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;

public final class JCFUserStatusRepository extends AbstractJCFRepository<UserStatus>
        implements UserStatusRepository {

    public JCFUserStatusRepository() {
        super(UserStatus.class, UserStatus::copy);
    }
}
