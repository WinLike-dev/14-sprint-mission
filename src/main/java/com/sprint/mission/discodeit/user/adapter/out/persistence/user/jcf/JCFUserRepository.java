package com.sprint.mission.discodeit.user.adapter.out.persistence.user.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;

// 파생 조회는 UserRepository의 default 구현을 그대로 쓴다.
public final class JCFUserRepository extends AbstractJCFRepository<User>
        implements UserRepository {

    public JCFUserRepository() {
        super(User.class, User::copy);
    }
}
