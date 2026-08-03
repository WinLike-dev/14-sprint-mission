package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;

public class UserRepository extends AbstractCrudRepository<User> {

    public UserRepository(ObjectStore<User> objectStore) {
        super(objectStore);
    }

    @Override
    protected Class<User> entityType() {
        return User.class;
    }
}
