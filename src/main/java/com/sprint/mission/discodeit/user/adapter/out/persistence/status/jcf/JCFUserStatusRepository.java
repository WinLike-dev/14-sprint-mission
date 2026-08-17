package com.sprint.mission.discodeit.user.adapter.out.persistence.status.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;

// 파생 조회는 UserStatusRepository의 default 구현을 그대로 쓴다.
public final class JCFUserStatusRepository extends AbstractJCFRepository<UserStatus>
        implements UserStatusRepository {

    public JCFUserStatusRepository() {
        super(UserStatus.class, UserStatus::copy);
    }
}
