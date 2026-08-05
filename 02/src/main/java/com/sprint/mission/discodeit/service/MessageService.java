package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, UUID channelId, UUID senderId, UUID receiverId);

    Message loadMessage(UUID id);

    List<Message> loadAllMessages();

    void updateMessage(UUID id, String content);

    void deleteMessage(UUID id);
}
