package com.sprint.mission.discodeit.message.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;

public final class JCFMessageRepository extends AbstractJCFRepository<Message>
        implements MessageRepository {

    public JCFMessageRepository() {
        super(Message.class, Message::copy);
    }
}
