package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageMap;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        messageMap = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID senderId, UUID receiverId) {
        channelService.readChannel(channelId);
        userService.readUser(senderId);
        userService.readUser(receiverId);

        Message message = new Message(content, channelId, senderId, receiverId);
        messageMap.put(message.getId(), message);
        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        Message message = messageMap.get(id);

        if (message == null) {
            throw new NoSuchElementException("메시지를 찾을 수 없습니다: " + id);
        }
        return message;
    }

    @Override
    public List<Message> readAllMessages() {
        return messageMap.values().stream()
                .toList();
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = readMessage(id);
        message.update(content);
    }

    @Override
    public void deleteMessage(UUID id) {
        readMessage(id);
        messageMap.remove(id);
    }
}
