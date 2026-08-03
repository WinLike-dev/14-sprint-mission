package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.objectStore.ObjectStore;

public class MessageRepository extends AbstractCrudRepository<Message> {

    public MessageRepository(ObjectStore<Message> objectStore) {
        super(objectStore);
    }

    @Override
    protected Class<Message> entityType() {
        return Message.class;
    }
}
