package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.message.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message> {

    List<Message> findAllByChannelId(UUID channelId);

    Optional<Message> findLatestByChannelId(UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
