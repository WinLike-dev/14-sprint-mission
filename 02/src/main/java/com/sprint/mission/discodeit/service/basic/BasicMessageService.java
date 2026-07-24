package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.CrudRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final CrudRepository<Message> messageRepository;
    private final CrudRepository<User> userRepository;
    private final CrudRepository<Channel> channelRepository;

    public BasicMessageService(
            CrudRepository<Message> messageRepository,
            CrudRepository<User> userRepository,
            CrudRepository<Channel> channelRepository
    ) {
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.channelRepository = Objects.requireNonNull(channelRepository);
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID senderId, UUID receiverId) {
        // 설계: 연관 데이터 존재 확인은 저장 기술과 무관한 비즈니스 규칙이다.
        channelRepository.findById(channelId);
        userRepository.findById(senderId);
        userRepository.findById(receiverId);

        Message message = new Message(content, channelId, senderId, receiverId);
        return messageRepository.create(message);
    }

    @Override
    public Message readMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> readAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = messageRepository.findById(id);
        message.update(content);
        messageRepository.update(message);
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }
}
