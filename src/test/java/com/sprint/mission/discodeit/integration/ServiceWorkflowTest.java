package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.message.dto.response.MessageDto;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.userstatus.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.common.exception.DuplicateRequestValueException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.auth.service.AuthControllerService;
import com.sprint.mission.discodeit.channel.service.ChannelControllerService;
import com.sprint.mission.discodeit.message.service.MessageControllerService;
import com.sprint.mission.discodeit.user.service.UserControllerService;
import com.sprint.mission.discodeit.userstatus.service.UserStatusControllerService;
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
        UserDto author = userControllerService.create(
                new UserCreateRequest("author", "author@example.com", "password"),
                new BinaryContentCreateRequest(
                        "profile.png", "image/png", new byte[]{1, 2, 3}
                )
        );
        UserDto participant = userControllerService.create(
                new UserCreateRequest("participant", "participant@example.com", "password"),
                null
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
                        List.of(new BinaryContentCreateRequest(
                                "attachment.txt", "text/plain", new byte[]{4, 5}
                        ))
                )
        );

        assertEquals(1, message.attachmentIds().size());
        assertNotNull(channelControllerService.find(channel.id()).lastMessageAt());

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
        UserDto participant = userControllerService.create(
                new UserCreateRequest(
                        "participant-" + suffix,
                        "participant-" + suffix + "@example.com",
                        "password"
                ),
                null
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
    void invalidUserEmailCleansUpCreatedProfile() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userControllerService.create(
                        new UserCreateRequest("invalid-email-user", "invalid-email", "password"),
                        new BinaryContentCreateRequest(
                                "profile.png", "image/png", new byte[]{1, 2, 3}
                        )
                )
        );

        assertTrue(binaryContentRepository.findAll().isEmpty());
    }
}
