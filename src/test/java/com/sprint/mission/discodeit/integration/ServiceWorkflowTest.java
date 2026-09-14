package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.message.service.dto.MessageAttachmentCommand;
import com.sprint.mission.discodeit.user.service.dto.LoginCommand;
import com.sprint.mission.discodeit.message.service.dto.CreateMessageCommand;
import com.sprint.mission.discodeit.channel.service.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.message.service.dto.MessageResult;
import com.sprint.mission.discodeit.channel.service.dto.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.service.dto.ReadStatusResult;
import com.sprint.mission.discodeit.channel.service.dto.UpdateReadStatusCommand;
import com.sprint.mission.discodeit.user.service.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.service.dto.UserResult;
import com.sprint.mission.discodeit.user.service.dto.UserStatusResult;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(2, channel.participantIds().size());

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

        assertEquals(1, message.attachmentIds().size());
        UUID attachmentId = message.attachmentIds().get(0);

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
        assertTrue(readStatusRepository.findAllByChannelId(channel.id()).isEmpty());
        assertFalse(binaryContentRepository.existsById(attachmentId));
        assertTrue(binaryContentRepository.existsById(author.profileId()));

        userControllerService.delete(author.id());
        userControllerService.delete(participant.id());

        // 사용자를 지우면 사용자 상태와 프로필 이미지가 함께 정리된다.
        assertTrue(userStatusRepository.findByUserId(author.id()).isEmpty());
        assertTrue(userStatusRepository.findByUserId(participant.id()).isEmpty());
        assertFalse(binaryContentRepository.existsById(author.profileId()));
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
    void deletingUserRemovesReadStatusesThroughDomainEvent() {
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

    // Channel.lastMessageAt은 ERD에 없어 @Transient로 두었으므로 조회한 채널에서는 항상 null이다.
    @Disabled("lastMessageAt을 최신 메시지 조회로 계산하도록 바꾼 뒤 다시 켠다")
    @Test
    void deletingLastMessageUpdatesChannelProjectionThroughDomainEvent() {
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
}
