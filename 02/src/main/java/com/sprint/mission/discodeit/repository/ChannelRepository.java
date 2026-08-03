package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;

public class ChannelRepository extends AbstractCrudRepository<Channel> {

    public ChannelRepository(ObjectStore<Channel> objectStore) {

        super(objectStore);
    }

    @Override
    protected Class<Channel> entityType() {
        return Channel.class;
    }
}
