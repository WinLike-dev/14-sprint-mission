package com.sprint.mission.discodeit.message.repository.jcf;

import com.sprint.mission.discodeit.common.repository.jcf.AbstractJCFRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JCFMessageRepository extends AbstractJCFRepository<Message>
        implements MessageRepository {

    public JCFMessageRepository() {
        super(Message.class, Message::copy);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Optional<Message> findLatestByChannelId(UUID channelId) {
        return findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(message -> deleteById(message.getId()));
    }
}
