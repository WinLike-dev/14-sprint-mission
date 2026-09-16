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
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager;
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager.FileToSave;
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
 * 채널/작성자 검증은 각 저장소를 직접 사용하고, 첨부파일 행은 Message의 cascade로 함께 저장·삭제된다.
 * 첨부파일의 실제 파일은 BinaryContentFileManager가 트랜잭션에 맞춰 다룬다.
 * 읽기는 클래스에 걸린 readOnly 트랜잭션에서, 쓰기는 메서드의 @Transactional에서 처리한다.
 */
@Service
@RequiredArgsConstructor
// 기본은 읽기 전용 트랜잭션이다. 쓰기 메서드만 @Transactional로 덮어쓴다.
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageControllerService {

    private final MessageRepository messageRepository;             // 메시지 저장소
    private final UserRepository userRepository;                   // 작성자 존재 확인용
    private final ChannelRepository channelRepository;             // 채널 존재 확인용
    private final BinaryContentFileManager fileManager;            // 첨부파일의 실제 파일 저장/삭제용

    // 새 메시지를 생성한다. 채널/작성자 존재 확인 후 첨부파일을 먼저 저장하고, 메시지를 저장한다.
    // 중간에 실패하면 트랜잭션이 롤백되어 저장한 행이 사라지고, 저장한 파일은 fileManager가 지운다.
    @Override
    @Transactional
    public MessageResult create(CreateMessageCommand command) {
        CreateMessageCommand target = Objects.requireNonNull(command);
        requireChannelExists(target.channelId());   // 채널이 존재하지 않으면 예외 발생
        requireAuthorExists(target.authorId());     // 작성자가 존재하지 않으면 예외 발생

        List<BinaryContent> attachments = toBinaryContents(target.attachments());
        // 존재는 위에서 확인했으므로 SELECT 없이 프록시 참조만 얻는다.
        // findById로 불러오면 지연 로딩이 안 되는 User.status까지 조회가 따라온다.
        Message message = new Message(
                target.content(),
                channelRepository.getReferenceById(target.channelId()),
                userRepository.getReferenceById(target.authorId()),
                attachments
        );
        // persist가 cascade되어 첨부 행도 이 시점에 저장되고 id를 받는다.
        Message created = messageRepository.save(message);
        fileManager.saveAll(toFilesToSave(attachments, target.attachments()));
        return MessageResult.from(created);
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
    @Transactional
    public MessageResult update(UUID id, UpdateMessageCommand command) {
        Message message = getMessage(id);
        message.update(Objects.requireNonNull(command).content());
        // 영속 상태라 변경 감지로 반영되므로 save를 부르지 않는다.
        return MessageResult.from(message);
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

    // 첨부 입력을 메타 정보만 가진 BinaryContent로 만든다. 저장은 Message의 cascade가 맡는다.
    private List<BinaryContent> toBinaryContents(List<MessageAttachmentCommand> attachments) {
        return attachments.stream()
                .map(attachment -> new BinaryContent(
                        attachment.fileName(), attachment.bytes().length, attachment.contentType()
                ))
                .toList();
    }

    // 저장된 첨부의 id와 업로드된 바이트를 순서대로 짝지어 파일 저장 목록을 만든다.
    private List<FileToSave> toFilesToSave(
            List<BinaryContent> attachments,
            List<MessageAttachmentCommand> commands
    ) {
        List<FileToSave> files = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            files.add(new FileToSave(attachments.get(i).getId(), commands.get(i).bytes()));
        }
        return files;
    }

    // 메시지를 삭제한다. 첨부 행은 cascade REMOVE로 함께 지워지고, 실제 파일만 따로 정리한다.
    private void deleteMessage(Message message) {
        List<UUID> attachmentIds = message.getAttachments().stream()
                .map(BinaryContent::getId)
                .toList();
        messageRepository.delete(message);
        fileManager.deleteAllAfterCommit(attachmentIds); // 실제 파일은 커밋이 확정된 뒤 삭제한다
    }
}
