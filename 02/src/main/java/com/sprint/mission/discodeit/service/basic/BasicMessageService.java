package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Message createMessage(String content, UUID channelId, UUID senderId, UUID receiverId) {
        validateMessageInput(content, channelId, senderId, receiverId);
        validateRelatedEntities(channelId, senderId, receiverId);

        Message message = new Message(
                content,
                channelId,
                senderId,
                receiverId
        );

        return messageRepository.create(message);
    }

    @Override
    public Message loadMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> loadAllMessages() {
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

    private void validateMessageInput(
            String content,
            UUID channelId,
            UUID senderId,
            UUID receiverId
    ) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "content은(는) 비어 있을 수 없습니다."
            );
        }
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        Objects.requireNonNull(senderId, "senderId는 null일 수 없습니다.");
        Objects.requireNonNull(receiverId, "receiverId는 null일 수 없습니다.");
    }

    private void validateRelatedEntities(
            UUID channelId,
            UUID senderId,
            UUID receiverId
    ) {
        if (!channelRepository.existsById(channelId)) {
            throw new EntityNotFoundException(Channel.class, channelId);
        }
        if (!userRepository.existsById(senderId)) {
            throw new EntityNotFoundException(User.class, senderId);
        }
        if (!userRepository.existsById(receiverId)) {
            throw new EntityNotFoundException(User.class, receiverId);
        }
    }
}
