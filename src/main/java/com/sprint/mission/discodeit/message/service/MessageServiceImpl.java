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
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * MessageControllerService의 구현체.
 * 메시지 생성/조회/수정/삭제의 실제 비즈니스 로직을 담당한다.
 * 채널/작성자 검증과 첨부파일 관리는 각 저장소를 직접 사용하고,
 * 첨부파일의 실제 파일은 BinaryContentFileManager가 트랜잭션에 맞춰 다룬다.
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageControllerService {

    private final MessageRepository messageRepository;             // 메시지 저장소
    private final UserRepository userRepository;                   // 작성자 존재 확인용
    private final ChannelRepository channelRepository;             // 채널 존재 확인용
    private final BinaryContentRepository binaryContentRepository; // 첨부파일 메타 정보 생성/삭제용
    private final BinaryContentFileManager fileManager;            // 첨부파일의 실제 파일 저장/삭제용

    // 새 메시지를 생성한다. 채널/작성자 존재 확인 후 첨부파일을 먼저 저장하고, 메시지를 저장한다.
    // 중간에 실패하면 트랜잭션이 롤백되어 저장한 행이 사라지고, 저장한 파일은 fileManager가 지운다.
    @Override
    @Transactional
    public MessageResult create(CreateMessageCommand command) {
        CreateMessageCommand target = Objects.requireNonNull(command);
        requireChannelExists(target.channelId());   // 채널이 존재하지 않으면 예외 발생
        requireAuthorExists(target.authorId());     // 작성자가 존재하지 않으면 예외 발생

        List<UUID> attachmentIds = createAttachments(target.attachments()); // 첨부파일들을 먼저 저장
        Message message = new Message(
                target.content(), target.channelId(), target.authorId(), attachmentIds
        );
        return MessageResult.from(messageRepository.save(message));
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
    // 첨부 목록(지연 로딩)을 읽고 파일 삭제를 커밋 뒤로 미루려면 트랜잭션이 필요하다.
    @Override
    @Transactional
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

    // 첨부파일마다 메타 정보를 저장해 id를 받고, 그 id로 실제 파일을 저장한다. 생성된 ID 목록을 반환한다.
    private List<UUID> createAttachments(List<MessageAttachmentCommand> attachments) {
        List<UUID> createdIds = new ArrayList<>();
        for (MessageAttachmentCommand attachment : attachments) {
            BinaryContent content = binaryContentRepository.save(new BinaryContent(
                    attachment.fileName(), attachment.bytes().length, attachment.contentType()
            ));
            fileManager.save(content.getId(), attachment.bytes());
            createdIds.add(content.getId());
        }
        return List.copyOf(createdIds);
    }

    // 메시지에 연결된 첨부파일들을 먼저 삭제한 뒤, 메시지를 삭제한다
    private void deleteMessage(Message message) {
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
            fileManager.deleteAfterCommit(attachmentId); // 실제 파일은 커밋이 확정된 뒤 삭제한다
        }
        messageRepository.deleteById(message.getId());
    }

}
