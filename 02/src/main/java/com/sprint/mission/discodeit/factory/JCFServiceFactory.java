package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.objectStore.JCFObjectStore;

public class JCFServiceFactory extends AbstractServiceFactory {

    public JCFServiceFactory() {
        super(
                new JCFObjectStore<User>(User::copy),
                new JCFObjectStore<Channel>(Channel::copy),
                new JCFObjectStore<Message>(Message::copy)
        );
    }
}
