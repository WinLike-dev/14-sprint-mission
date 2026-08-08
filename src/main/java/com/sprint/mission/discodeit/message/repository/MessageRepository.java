package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.message.entity.Message;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message> {

    default List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    default Optional<Message> findLatestByChannelId(UUID channelId) {
        return findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt));
    }

    default void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(message -> deleteById(message.getId()));
    }
}
