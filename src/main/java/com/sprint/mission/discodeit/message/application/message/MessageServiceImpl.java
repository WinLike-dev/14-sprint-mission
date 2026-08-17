package com.sprint.mission.discodeit.message.application.message;

import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageAttachmentCreateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageAuthorReader;
import com.sprint.mission.discodeit.message.application.port.out.MessageChannelReader;
import com.sprint.mission.discodeit.message.application.port.out.MessageContentManager;
import com.sprint.mission.discodeit.message.application.port.out.MessageContentData;
import com.sprint.mission.discodeit.message.api.event.ChannelMessageChangedEvent;
import com.sprint.mission.discodeit.common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * MessageControllerService의 구현체.
 * 메시지 생성/조회/수정/삭제의 실제 비즈니스 로직을 담당한다.
 * 채널/작성자 검증과 첨부파일 관리는 outbound 포트(Reader, Manager)를 통해 나간다.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageControllerService {

    private final MessageRepository messageRepository;   // 메시지 저장소 outbound 포트
    private final MessageAuthorReader authorReader;       // 작성자 존재 확인 outbound 포트
    private final MessageChannelReader channelReader;     // 채널 존재 확인 outbound 포트
    private final MessageContentManager contentManager;   // 첨부파일 생성/삭제 outbound 포트

    // 새 메시지를 생성한다. 채널/작성자 존재 확인 후 첨부파일을 먼저 저장하고, 메시지를 저장한다.
    @Override
    public MessageDto create(MessageCreateRequest request) {
        MessageCreateRequest target = Objects.requireNonNull(request);
        channelReader.requireExists(target.channelId());   // 채널이 존재하지 않으면 예외 발생
        authorReader.requireExists(target.authorId());     // 작성자가 존재하지 않으면 예외 발생

        List<UUID> attachmentIds = createAttachments(target.attachments()); // 첨부파일들을 먼저 저장
        Message message = new Message(
                target.content(), target.channelId(), target.authorId(), attachmentIds
        );
        Message created = null;
        try {
            created = messageRepository.create(message);
            // 채널에 새 메시지가 추가되었음을 이벤트로 알린다 (채널의 lastMessageAt 갱신 등에 사용)
            Events.raise(new ChannelMessageChangedEvent(
                    created.getChannelId(), created.getCreatedAt()
            ));
            return MessageDto.from(created);
        } catch (RuntimeException exception) {
            // 메시지 저장 실패 시 이미 저장된 메시지와 첨부파일을 정리(보상 로직)한다
            if (created != null && messageRepository.existsById(created.getId())) {
                Message saved = created;
                suppressCleanupFailure(
                        exception,
                        () -> messageRepository.deleteById(saved.getId())
                );
            }
            cleanupAttachments(attachmentIds, exception); // 이미 생성된 첨부파일도 삭제
            throw exception;
        }
    }

    // 특정 채널의 모든 메시지를 조회한다
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        channelReader.requireExists(channelId); // 채널 존재 여부 먼저 확인
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageDto::from)
                .toList();
    }

    // 메시지 내용을 수정한다
    @Override
    public MessageDto update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.getById(id);
        message.update(Objects.requireNonNull(request).content());
        return MessageDto.from(messageRepository.update(message));
    }

    // 메시지를 삭제하고, 채널에 메시지 변경 이벤트를 발행한다
    @Override
    public void delete(UUID id) {
        Message message = messageRepository.getById(id);
        UUID channelId = message.getChannelId();
        deleteMessage(message);
        // 삭제 후 해당 채널의 최신 메시지 시각을 이벤트로 전달 (채널의 lastMessageAt 갱신용)
        Events.raise(new ChannelMessageChangedEvent(
                channelId,
                messageRepository.findLatestByChannelId(channelId)
                        .map(Message::getCreatedAt)
                        .orElse(null) // 메시지가 하나도 없으면 null
        ));
    }

    // 첨부파일 목록을 순회하며 BinaryContent를 생성하고, 생성된 ID 목록을 반환한다
    private List<UUID> createAttachments(List<MessageAttachmentCreateRequest> requests) {
        List<UUID> createdIds = new ArrayList<>();
        try {
            for (MessageAttachmentCreateRequest request : requests) {
                createdIds.add(contentManager.create(new MessageContentData(
                        request.fileName(), request.contentType(), request.bytes()
                )));
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
            contentManager.delete(attachmentId);
        }
        messageRepository.deleteById(message.getId());
    }

    // 이미 생성된 첨부파일들을 하나씩 삭제한다 (실패 시 원본 예외에 suppressed로 추가)
    private void cleanupAttachments(List<UUID> attachmentIds, RuntimeException original) {
        for (UUID attachmentId : attachmentIds) {
            suppressCleanupFailure(original, () -> contentManager.delete(attachmentId));
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
