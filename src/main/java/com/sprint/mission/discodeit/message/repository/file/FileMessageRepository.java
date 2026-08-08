package com.sprint.mission.discodeit.message.repository.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;

import java.nio.file.Path;

public final class FileMessageRepository extends AbstractFileRepository<Message>
        implements MessageRepository {

    public FileMessageRepository(Path root) {
        super(root.resolve("messages"), Message.class);
    }
}
