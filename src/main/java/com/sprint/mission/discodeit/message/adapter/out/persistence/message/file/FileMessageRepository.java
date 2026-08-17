package com.sprint.mission.discodeit.message.adapter.out.persistence.message.file;

import com.sprint.mission.discodeit.common.repository.file.AbstractFileRepository;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class FileMessageRepository extends AbstractFileRepository<Message>
        implements MessageRepository {

    public FileMessageRepository(Path root) {
        super(root.resolve("messages"), Message.class);
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
