package com.sprint.mission.discodeit.message.adapter.out.persistence.message.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;

import java.nio.file.Path;

// 파생 조회는 MessageRepository의 default 구현을 그대로 쓴다.
public final class FileMessageRepository extends AbstractFileRepository<Message>
        implements MessageRepository {

    public FileMessageRepository(Path root) {
        super(root.resolve("messages"), Message.class);
    }
}
