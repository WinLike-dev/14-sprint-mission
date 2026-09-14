package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.service.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.message.service.dto.MessageAttachmentCommand;
import com.sprint.mission.discodeit.message.service.dto.MessageResult;
import com.sprint.mission.discodeit.message.service.dto.UpdateMessageCommand;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.content.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * MessageControllerService의 구현체.
 * 메시지 생성/조회/수정/삭제의 실제 비즈니스 로직을 담당한다.
 * 채널/작성자 검증과 첨부파일 관리는 각 저장소를 직접 사용한다.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageControllerService {

    private final MessageRepository messageRepository;             // 메시지 저장소
    private final UserRepository userRepository;                   // 작성자 존재 확인용
    private final ChannelRepository channelRepository;             // 채널 존재 확인용
    private final BinaryContentRepository binaryContentRepository; // 첨부파일 생성/삭제용

    // 새 메시지를 생성한다. 채널/작성자 존재 확인 후 첨부파일을 먼저 저장하고, 메시지를 저장한다.
    @Override
    public MessageResult create(CreateMessageCommand command) {
        CreateMessageCommand target = Objects.requireNonNull(command);
        requireChannelExists(target.channelId());   // 채널이 존재하지 않으면 예외 발생
        requireAuthorExists(target.authorId());     // 작성자가 존재하지 않으면 예외 발생

        List<UUID> attachmentIds = createAttachments(target.attachments()); // 첨부파일들을 먼저 저장
        Message message = new Message(
                target.content(), target.channelId(), target.authorId(), attachmentIds
        );
        try {
            return MessageResult.from(messageRepository.save(message));
        } catch (RuntimeException exception) {
            // 메시지 저장에 실패하면 먼저 저장한 첨부파일을 정리(보상 로직)한다
            cleanupAttachments(attachmentIds, exception);
            throw exception;
        }
    }

    // 특정 채널의 모든 메시지를 조회한다
    @Override
    public List<MessageResult> findAllByChannelId(UUID channelId) {
        requireChannelExists(channelId); // 채널 존재 여부 먼저 확인
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResult::from)
                .toList();
    }

    // 메시지 내용을 수정한다
    @Override
    public MessageResult update(UUID id, UpdateMessageCommand command) {
        Message message = getMessage(id);
        message.update(Objects.requireNonNull(command).content());
        return MessageResult.from(messageRepository.save(message));
    }

    // 메시지를 삭제한다. 채널의 마지막 메시지 시각은 채널 조회 때 메시지에서 다시 구하므로 따로 갱신하지 않는다.
    @Override
    public void delete(UUID id) {
        deleteMessage(getMessage(id));
    }

    // ID로 메시지를 조회하고, 없으면 예외를 던지는 헬퍼 메서드
    private Message getMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Message.class, id));
    }

    // 채널이 존재하는지 확인하고, 없으면 예외를 던지는 헬퍼 메서드
    private void requireChannelExists(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new EntityNotFoundException(Channel.class, channelId);
        }
    }

    // 작성자가 존재하는지 확인하고, 없으면 예외를 던지는 헬퍼 메서드
    private void requireAuthorExists(UUID authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException(User.class, authorId);
        }
    }

    // 첨부파일 목록을 순회하며 BinaryContent를 생성하고, 생성된 ID 목록을 반환한다
    private List<UUID> createAttachments(List<MessageAttachmentCommand> attachments) {
        List<UUID> createdIds = new ArrayList<>();
        try {
            for (MessageAttachmentCommand attachment : attachments) {
                BinaryContent content = new BinaryContent(
                        attachment.fileName(), attachment.contentType(), attachment.bytes()
                );
                createdIds.add(binaryContentRepository.save(content).getId());
            }
            return List.copyOf(createdIds);
        } catch (RuntimeException exception) {
            cleanupAttachments(createdIds, exception); // 중간에 실패하면 이미 생성된 첨부파일을 정리
            throw exception;
        }
    }

    // 메시지에 연결된 첨부파일들을 먼저 삭제한 뒤, 메시지를 삭제한다
    private void deleteMessage(Message message) {
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }
        messageRepository.deleteById(message.getId());
    }

    // 이미 생성된 첨부파일들을 하나씩 삭제한다 (실패 시 원본 예외에 suppressed로 추가)
    private void cleanupAttachments(List<UUID> attachmentIds, RuntimeException original) {
        for (UUID attachmentId : attachmentIds) {
            suppressCleanupFailure(original, () -> binaryContentRepository.deleteById(attachmentId));
        }
    }

    // 정리 작업 중 발생한 예외를 원본 예외에 suppressed로 추가한다 (원본 예외가 묻히지 않도록)
    private void suppressCleanupFailure(RuntimeException original, Runnable cleanup) {
        try {
            cleanup.run();
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
