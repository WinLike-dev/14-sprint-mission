package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageAttachmentCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.message.adapter.in.rest.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserResult;
import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.common.exception.DuplicateRequestValueException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.domain.readstatus.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import com.sprint.mission.discodeit.user.application.auth.AuthControllerService;
import com.sprint.mission.discodeit.channel.application.channel.ChannelControllerService;
import com.sprint.mission.discodeit.message.application.message.MessageControllerService;
import com.sprint.mission.discodeit.channel.application.readstatus.ReadStatusControllerService;
import com.sprint.mission.discodeit.user.application.user.UserControllerService;
import com.sprint.mission.discodeit.user.application.status.UserStatusControllerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        UserResult author = userControllerService.create(
                new CreateUserCommand(
                        "author",
                        "author@example.com",
                        "password",
                        new UserProfileCommand("profile.png", "image/png", new byte[]{1, 2, 3})
                )
        );
        UserResult participant = userControllerService.create(
                new CreateUserCommand("participant", "participant@example.com", "password", null)
        );

        UserStatusDto foundStatus = userStatusControllerService.find(author.id());
        assertEquals(author.id(), foundStatus.userId());

        Instant beforeStatusUpdate = Instant.now();
        UserStatusDto updatedStatus = userStatusControllerService.update(author.id());
        Instant afterStatusUpdate = Instant.now();
        assertEquals(author.id(), updatedStatus.userId());
        assertFalse(updatedStatus.lastActiveAt().isBefore(beforeStatusUpdate));
        assertFalse(updatedStatus.lastActiveAt().isAfter(afterStatusUpdate));

        Instant beforeLogin = Instant.now();
        assertEquals(author.id(), authControllerService.login(
                new LoginRequest("author", "password")
        ).id());
        Instant afterLogin = Instant.now();
        UserStatusDto loginStatus = UserStatusDto.from(
                userStatusRepository.findByUserId(author.id()).orElseThrow()
        );
        assertFalse(loginStatus.lastActiveAt().isBefore(beforeLogin));
        assertFalse(loginStatus.lastActiveAt().isAfter(afterLogin));

        ChannelDto channel = channelControllerService.createPrivate(
                new PrivateChannelCreateRequest(List.of(author.id(), participant.id()))
        );
        assertEquals(2, channel.participantIds().size());

        MessageDto message = messageControllerService.create(
                new MessageCreateRequest(
                        "hello",
                        channel.id(),
                        author.id(),
                        List.of(new MessageAttachmentCreateRequest(
                                "attachment.txt", "text/plain", new byte[]{4, 5}
                        ))
                )
        );

        assertEquals(1, message.attachmentIds().size());
        assertNotNull(channelControllerService.find(channel.id()).lastMessageAt());

        ReadStatusDto foundReadStatus = readStatusControllerService.find(
                author.id(), channel.id()
        );
        Instant beforeReadStatusUpdate = Instant.now();
        ReadStatusDto updatedReadStatus = readStatusControllerService.updateLastReadAt(
                author.id(), channel.id()
        );
        Instant afterReadStatusUpdate = Instant.now();
        assertFalse(updatedReadStatus.lastReadAt().isBefore(foundReadStatus.lastReadAt()));
        assertFalse(updatedReadStatus.lastReadAt().isBefore(beforeReadStatusUpdate));
        assertFalse(updatedReadStatus.lastReadAt().isAfter(afterReadStatusUpdate));

        channelControllerService.delete(channel.id());

        assertTrue(messageRepository.findAll().isEmpty());
        assertTrue(readStatusRepository.findAll().isEmpty());
        assertEquals(1, binaryContentRepository.findAll().size());

        userControllerService.delete(author.id());
        userControllerService.delete(participant.id());

        assertTrue(userStatusRepository.findAll().isEmpty());
        assertTrue(binaryContentRepository.findAll().isEmpty());
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
                            new PrivateChannelCreateRequest(
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
                        new MessageCreateRequest(
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
                () -> userStatusControllerService.update(missingUserId)
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
        ChannelDto channel = channelControllerService.createPrivate(
                new PrivateChannelCreateRequest(List.of(participant.id()))
        );

        try {
            assertThrows(
                    ReadStatusCreationNotAllowedException.class,
                    () -> readStatusControllerService.create(participant.id(), channel.id())
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
        ChannelDto channel = channelControllerService.createPublic(
                new PublicChannelCreateRequest(
                        "event-channel-" + suffix,
                        "event cleanup test"
                )
        );
        readStatusControllerService.create(user.id(), channel.id());

        userControllerService.delete(user.id());

        assertTrue(readStatusRepository.findAllByUserId(user.id()).isEmpty());
        channelControllerService.delete(channel.id());
    }

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
        ChannelDto channel = channelControllerService.createPublic(
                new PublicChannelCreateRequest(
                        "message-event-" + suffix,
                        "message projection test"
                )
        );
        MessageDto message = messageControllerService.create(
                new MessageCreateRequest(
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

        assertTrue(binaryContentRepository.findAll().isEmpty());
    }
}
