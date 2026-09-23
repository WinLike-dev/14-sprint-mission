package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.message.service.dto.command.MessageAttachmentCommand;
import com.sprint.mission.discodeit.user.service.dto.command.LoginCommand;
import com.sprint.mission.discodeit.message.service.dto.command.CreateMessageCommand;
import com.sprint.mission.discodeit.channel.service.dto.result.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.message.service.dto.result.MessageResult;
import com.sprint.mission.discodeit.channel.service.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.service.dto.result.ReadStatusResult;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.user.service.dto.command.CreateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.command.UpdateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.command.UserProfileCommand;
import com.sprint.mission.discodeit.user.service.dto.result.UserResult;
import com.sprint.mission.discodeit.user.service.dto.result.UserStatusResult;
import com.sprint.mission.discodeit.common.exception.exceptions.DuplicateRequestValueException;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.content.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.channel.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import com.sprint.mission.discodeit.user.service.AuthControllerService;
import com.sprint.mission.discodeit.channel.service.ChannelControllerService;
import com.sprint.mission.discodeit.message.service.MessageControllerService;
import com.sprint.mission.discodeit.channel.service.ReadStatusControllerService;
import com.sprint.mission.discodeit.user.service.UserControllerService;
import com.sprint.mission.discodeit.user.service.UserStatusControllerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import com.sprint.mission.discodeit.content.storage.StorageProperties;
import com.sprint.mission.discodeit.message.entity.Message;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

// application.yml의 datasource(PostgreSQL)에 실제로 연결하는 통합 테스트다.
// 같은 DB를 개발 중에도 쓰므로 테이블을 비우지 않는다.
// 대신 이름에 무작위 suffix를 붙이고, "전체가 비었다"가 아니라 "이 테스트가 만든 데이터가 사라졌다"를 단언한다.
@SpringBootTest
class ServiceWorkflowTest {

    @Autowired
    private UserControllerService userControllerService;

    @Autowired
    private AuthControllerService authControllerService;

    @Autowired
    private ChannelControllerService channelControllerService;

    @Autowired
    private MessageControllerService messageControllerService;

    @Autowired
    private ReadStatusControllerService readStatusControllerService;

    @Autowired
    private UserStatusControllerService userStatusControllerService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private StorageProperties storageProperties;

    @Test
    void userChannelMessageLifecycleUsesInternalCollaborators() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "author-" + suffix,
                        "author-" + suffix + "@example.com",
                        "password",
                        new UserProfileCommand("profile.png", "image/png", new byte[]{1, 2, 3})
                )
        );
        UserResult participant = userControllerService.create(
                new CreateUserCommand(
                        "participant-" + suffix,
                        "participant-" + suffix + "@example.com",
                        "password",
                        null
                )
        );

        UserStatusResult foundStatus = userStatusControllerService.find(author.id());
        assertEquals(author.id(), foundStatus.userId());

        Instant newLastActiveAt = foundStatus.lastActiveAt().plusSeconds(60);
        UserStatusResult updatedStatus =
                userStatusControllerService.update(author.id(), newLastActiveAt);
        assertEquals(author.id(), updatedStatus.userId());
        assertEquals(newLastActiveAt, updatedStatus.lastActiveAt());

        Instant beforeLogin = Instant.now();
        assertEquals(author.id(), authControllerService.login(
                new LoginCommand("author-" + suffix, "password")
        ).id());
        Instant afterLogin = Instant.now();
        UserStatusResult loginStatus = UserStatusResult.from(
                userStatusRepository.findByUserId(author.id()).orElseThrow()
        );
        assertFalse(loginStatus.lastActiveAt().isBefore(beforeLogin));
        assertFalse(loginStatus.lastActiveAt().isAfter(afterLogin));

        ChannelResult channel = channelControllerService.createPrivate(
                new CreatePrivateChannelCommand(List.of(author.id(), participant.id()))
        );
        assertEquals(2, channel.participants().size());

        MessageResult message = messageControllerService.create(
                new CreateMessageCommand(
                        "hello",
                        channel.id(),
                        author.id(),
                        List.of(new MessageAttachmentCommand(
                                "attachment.txt", "text/plain", new byte[]{4, 5}
                        ))
                )
        );

        assertEquals(1, message.attachments().size());
        assertEquals("attachment.txt", message.attachments().get(0).fileName());
        assertEquals(author.id(), message.author().id());
        UUID attachmentId = message.attachments().get(0).id();

        ReadStatusResult foundReadStatus = readStatusControllerService.find(
                author.id(), channel.id()
        );
        Instant newLastReadAt = foundReadStatus.lastReadAt().plusSeconds(60);
        ReadStatusResult updatedReadStatus = readStatusControllerService.updateLastReadAt(
                foundReadStatus.id(), new UpdateReadStatusCommand(newLastReadAt)
        );
        assertEquals(newLastReadAt, updatedReadStatus.lastReadAt());

        channelControllerService.delete(channel.id());

        // 채널을 지우면 메시지, 읽음 상태, 메시지 첨부파일이 함께 정리되고 프로필은 남는다.
        assertTrue(messageRepository.findAllByChannelId(channel.id()).isEmpty());
        assertTrue(readStatusRepository.findParticipantsByChannelIdIn(List.of(channel.id())).isEmpty());
        assertFalse(binaryContentRepository.existsById(attachmentId));
        assertTrue(binaryContentRepository.existsById(author.profile().id()));

        userControllerService.delete(author.id());
        userControllerService.delete(participant.id());

        // 사용자를 지우면 사용자 상태와 프로필 이미지가 함께 정리된다.
        assertTrue(userStatusRepository.findByUserId(author.id()).isEmpty());
        assertTrue(userStatusRepository.findByUserId(participant.id()).isEmpty());
        assertFalse(binaryContentRepository.existsById(author.profile().id()));
    }

    @Test
    void duplicatePrivateChannelParticipantsUseRequestValueException() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult participant = userControllerService.create(
                new CreateUserCommand(
                        "participant-" + suffix,
                        "participant-" + suffix + "@example.com",
                        "password",
                        null
                )
        );

        try {
            assertThrows(
                    DuplicateRequestValueException.class,
                    () -> channelControllerService.createPrivate(
                            new CreatePrivateChannelCommand(
                                    List.of(participant.id(), participant.id())
                            )
                    )
            );
        } finally {
            userControllerService.delete(participant.id());
        }
    }

    @Test
    void missingMessageChannelUsesEntityNotFoundException() {
        assertThrows(
                EntityNotFoundException.class,
                () -> messageControllerService.create(
                        new CreateMessageCommand(
                                "hello",
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                List.of()
                        )
                )
        );
    }

    @Test
    void missingUserStatusUsesEntityNotFoundException() {
        UUID missingUserId = UUID.randomUUID();

        assertThrows(
                EntityNotFoundException.class,
                () -> userStatusControllerService.find(missingUserId)
        );
        assertThrows(
                EntityNotFoundException.class,
                () -> userStatusControllerService.update(missingUserId, Instant.now())
        );
    }

    @Test
    void missingReadStatusUsesEntityNotFoundException() {
        assertThrows(
                EntityNotFoundException.class,
                () -> readStatusControllerService.find(
                        UUID.randomUUID(), UUID.randomUUID()
                )
        );
    }

    @Test
    void privateChannelReadStatusCannotBeCreatedOutsideChannelCreation() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult participant = userControllerService.create(
                new CreateUserCommand(
                        "private-member-" + suffix,
                        "private-member-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPrivate(
                new CreatePrivateChannelCommand(List.of(participant.id()))
        );

        try {
            assertThrows(
                    ReadStatusCreationNotAllowedException.class,
                    () -> readStatusControllerService.create(new CreateReadStatusCommand(
                            participant.id(), channel.id(), Instant.now()
                    ))
            );
        } finally {
            channelControllerService.delete(channel.id());
            userControllerService.delete(participant.id());
        }
    }

    @Test
    void deletingUserRemovesReadStatuses() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult user = userControllerService.create(
                new CreateUserCommand(
                        "event-user-" + suffix,
                        "event-user-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPublic(
                new CreatePublicChannelCommand(
                        "event-channel-" + suffix,
                        "event cleanup test"
                )
        );
        readStatusControllerService.create(new CreateReadStatusCommand(
                user.id(), channel.id(), Instant.now()
        ));

        userControllerService.delete(user.id());

        assertTrue(readStatusRepository.findAllByUserId(user.id()).isEmpty());
        channelControllerService.delete(channel.id());
    }

    // lastMessageAt은 채널에 저장하지 않고 조회할 때 메시지에서 구한다.
    @Test
    void deletingLastMessageClearsChannelLastMessageAt() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "event-author-" + suffix,
                        "event-author-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPublic(
                new CreatePublicChannelCommand(
                        "message-event-" + suffix,
                        "message projection test"
                )
        );
        MessageResult message = messageControllerService.create(
                new CreateMessageCommand(
                        "event message", channel.id(), author.id(), List.of()
                )
        );
        assertNotNull(channelControllerService.find(channel.id()).lastMessageAt());

        messageControllerService.delete(message.id());

        assertNull(channelControllerService.find(channel.id()).lastMessageAt());
        channelControllerService.delete(channel.id());
        userControllerService.delete(author.id());
    }

    @Test
    void invalidUserEmailCleansUpCreatedProfile() {
        long contentCountBefore = binaryContentRepository.count();

        assertThrows(
                IllegalArgumentException.class,
                () -> userControllerService.create(
                        new CreateUserCommand(
                                "invalid-email-user",
                                "invalid-email",
                                "password",
                                new UserProfileCommand("profile.png", "image/png", new byte[]{1, 2, 3})
                        )
                )
        );

        // 먼저 저장된 프로필 이미지가 정리되어 개수가 그대로여야 한다.
        assertEquals(contentCountBefore, binaryContentRepository.count());
    }

    // 작성자가 탈퇴해도 메시지는 남고 작성자 자리만 비워진다.
    @Test
    void deletingUserKeepsMessagesWithoutAuthor() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "leaving-" + suffix,
                        "leaving-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPublic(
                new CreatePublicChannelCommand("leftover-" + suffix, "작성자 정리 테스트")
        );
        MessageResult message = messageControllerService.create(
                new CreateMessageCommand("남는 메시지", channel.id(), author.id(), List.of())
        );

        userControllerService.delete(author.id());

        Message stored = messageRepository.findById(message.id()).orElseThrow();
        assertNull(stored.getAuthor());

        channelControllerService.delete(channel.id());
    }

    // 프로필을 바꾸면 이전 프로필 행과 파일이 함께 사라진다.
    @Test
    void replacingProfileRemovesPreviousProfile() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult created = userControllerService.create(
                new CreateUserCommand(
                        "profile-" + suffix,
                        "profile-" + suffix + "@example.com",
                        "password",
                        new UserProfileCommand("old.png", "image/png", new byte[]{1, 2, 3})
                )
        );
        UUID oldProfileId = created.profile().id();
        assertTrue(Files.exists(storedFile(oldProfileId)));

        UserResult updated = userControllerService.update(
                created.id(),
                new UpdateUserCommand(
                        null, null, null,
                        new UserProfileCommand("new.png", "image/png", new byte[]{4, 5, 6})
                )
        );

        assertNotEquals(oldProfileId, updated.profile().id());
        assertEquals("new.png", updated.profile().fileName());
        assertFalse(binaryContentRepository.existsById(oldProfileId));
        assertFalse(Files.exists(storedFile(oldProfileId)));
        assertTrue(Files.exists(storedFile(updated.profile().id())));

        userControllerService.delete(created.id());
        assertFalse(Files.exists(storedFile(updated.profile().id())));
    }

    // 메시지를 지우면 첨부 행과 실제 파일이 모두 사라진다.
    @Test
    void deletingMessageRemovesAttachmentFile() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "file-" + suffix,
                        "file-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPublic(
                new CreatePublicChannelCommand("file-channel-" + suffix, "첨부 파일 정리 테스트")
        );
        MessageResult message = messageControllerService.create(
                new CreateMessageCommand(
                        "첨부 포함", channel.id(), author.id(),
                        List.of(new MessageAttachmentCommand("note.txt", "text/plain", new byte[]{7, 8}))
                )
        );
        UUID attachmentId = message.attachments().get(0).id();
        assertTrue(Files.exists(storedFile(attachmentId)));

        messageControllerService.delete(message.id());

        assertFalse(binaryContentRepository.existsById(attachmentId));
        assertFalse(Files.exists(storedFile(attachmentId)));

        channelControllerService.delete(channel.id());
        userControllerService.delete(author.id());
    }

    // 메시지 목록은 최근 순으로 페이지 단위로 끊어서 준다.
    @Test
    void messagesArePagedFromNewest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "paged-" + suffix,
                        "paged-" + suffix + "@example.com",
                        "password",
                        null
                )
        );
        ChannelResult channel = channelControllerService.createPublic(
                new CreatePublicChannelCommand("paged-channel-" + suffix, "페이지네이션 테스트")
        );
        for (int index = 0; index < 3; index++) {
            messageControllerService.create(
                    new CreateMessageCommand("message-" + index, channel.id(), author.id(), List.of())
            );
        }

        Slice<MessageResult> firstPage = messageControllerService.findAllByChannelId(
                channel.id(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        Slice<MessageResult> secondPage = messageControllerService.findAllByChannelId(
                channel.id(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        assertEquals(2, firstPage.getContent().size());
        assertTrue(firstPage.hasNext());
        assertEquals(1, secondPage.getContent().size());
        assertFalse(secondPage.hasNext());
        // 최근 메시지가 먼저 온다.
        assertEquals("message-2", firstPage.getContent().get(0).content());
        assertEquals("message-0", secondPage.getContent().get(0).content());

        channelControllerService.delete(channel.id());
        userControllerService.delete(author.id());
    }

    // 스토리지에 저장된 파일 경로. LocalBinaryContentStorage의 규칙({root}/{id})과 같다.
    private Path storedFile(UUID binaryContentId) {
        return Path.of(storageProperties.local().rootPath())
                .toAbsolutePath()
                .normalize()
                .resolve(binaryContentId.toString());
    }
}
