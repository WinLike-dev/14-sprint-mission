package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.binarycontent.service.InternalBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageControllerService, InternalMessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final InternalBinaryContentService internalBinaryContentService;

    @Override
    public MessageDto create(MessageCreateRequest request) {
        MessageCreateRequest target = Objects.requireNonNull(request);
        channelRepository.findById(target.channelId());
        userRepository.findById(target.authorId());

        List<UUID> attachmentIds = createAttachments(target.attachments());
        Message message = new Message(
                target.content(), target.channelId(), target.authorId(), attachmentIds
        );
        try {
            return MessageDto.from(messageRepository.create(message));
        } catch (RuntimeException exception) {
            cleanupAttachments(attachmentIds, exception);
            throw exception;
        }
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelRepository.findById(channelId);
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageDto::from)
                .toList();
    }

    @Override
    public MessageDto update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id);
        message.update(Objects.requireNonNull(request).content());
        return MessageDto.from(messageRepository.update(message));
    }

    @Override
    public void delete(UUID id) {
        deleteMessage(messageRepository.findById(id));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        messageRepository.findAllByChannelId(channelId).forEach(this::deleteMessage);
    }

    private List<UUID> createAttachments(List<BinaryContentCreateRequest> requests) {
        List<UUID> createdIds = new ArrayList<>();
        try {
            for (BinaryContentCreateRequest request : requests) {
                BinaryContentDto content = internalBinaryContentService.create(request);
                createdIds.add(content.id());
            }
            return List.copyOf(createdIds);
        } catch (RuntimeException exception) {
            cleanupAttachments(createdIds, exception);
            throw exception;
        }
    }

    private void deleteMessage(Message message) {
        for (UUID attachmentId : message.getAttachmentIds()) {
            internalBinaryContentService.delete(attachmentId);
        }
        messageRepository.deleteById(message.getId());
    }

    private void cleanupAttachments(List<UUID> attachmentIds, RuntimeException original) {
        for (UUID attachmentId : attachmentIds) {
            try {
                internalBinaryContentService.delete(attachmentId);
            } catch (RuntimeException cleanupFailure) {
                original.addSuppressed(cleanupFailure);
            }
        }
    }
}
