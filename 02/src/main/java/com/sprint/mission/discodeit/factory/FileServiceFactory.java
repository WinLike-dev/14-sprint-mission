package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.objectStore.FileObjectStore;

import java.nio.file.Path;

public class FileServiceFactory extends AbstractServiceFactory {

    public FileServiceFactory() {
        super(
                new FileObjectStore<User>(
                        Path.of("data", "users"),
                        User.class
                ),
                new FileObjectStore<Channel>(
                        Path.of("data", "channels"),
                        Channel.class
                ),
                new FileObjectStore<Message>(
                        Path.of("data", "messages"),
                        Message.class
                )
        );
    }
}
