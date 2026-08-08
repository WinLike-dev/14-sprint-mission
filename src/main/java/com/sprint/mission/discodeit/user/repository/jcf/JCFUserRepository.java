package com.sprint.mission.discodeit.user.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;

public final class JCFUserRepository extends AbstractJCFRepository<User>
        implements UserRepository {

    public JCFUserRepository() {
        super(User.class, User::copy);
    }
}
