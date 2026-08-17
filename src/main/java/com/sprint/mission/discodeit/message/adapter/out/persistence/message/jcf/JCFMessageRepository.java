package com.sprint.mission.discodeit.message.adapter.out.persistence.message.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;

// 파생 조회는 MessageRepository의 default 구현을 그대로 쓴다.
public final class JCFMessageRepository extends AbstractJCFRepository<Message>
        implements MessageRepository {

    public JCFMessageRepository() {
        super(Message.class, Message::copy);
    }
}
