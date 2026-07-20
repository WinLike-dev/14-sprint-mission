package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, UUID channelId, UUID senderId, UUID receiverId);

    Message readMessage(UUID id);

    List<Message> readAllMessages();

    void updateMessage(UUID id, String content);

    void deleteMessage(UUID id);
}
