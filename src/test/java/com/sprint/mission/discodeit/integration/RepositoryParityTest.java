package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.message.domain.message.Message;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.StorageOperationException;
import com.sprint.mission.discodeit.content.application.port.out.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.message.application.port.out.MessageRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.file.FileChannelRepository;
import com.sprint.mission.discodeit.message.adapter.out.persistence.message.file.FileMessageRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.file.FileUserRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.status.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.content.adapter.out.persistence.binarycontent.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.channel.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.message.adapter.out.persistence.message.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.channel.adapter.out.persistence.readstatus.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.user.adapter.out.persistence.status.jcf.JCFUserStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryParityTest {

    @TempDir
    Path tempDirectory;

    @Test
    void jcfAndFileRepositoriesProvideTheSameDomainQueries() {
        List<Repositories> repositorySets = List.of(
                jcfRepositories(),
                fileRepositories(tempDirectory)
        );

        for (Repositories repositories : repositorySets) {
            verifyContract(repositories);
        }
    }

    @Test
    void corruptedFileIsReportedAsStorageOperationException() throws IOException {
        Path root = tempDirectory.resolve("corrupted-storage");
        Path users = Files.createDirectories(root.resolve("users"));
        Files.writeString(users.resolve(UUID.randomUUID() + ".ser"), "not serialized data");

        StorageOperationException exception = assertThrows(
                StorageOperationException.class,
                () -> new FileUserRepository(root).findAll()
        );

        assertNotNull(exception.getCause());
    }

    private void verifyContract(Repositories repositories) {
        User user = repositories.users.create(
                new User("woody", "woody@example.com", "password", null)
        );
        UserStatus userStatus = repositories.userStatuses.create(
                new UserStatus(user.getId(), Instant.now())
        );
        Channel channel = repositories.channels.create(Channel.privateChannel());
        ReadStatus readStatus = repositories.readStatuses.create(
                new ReadStatus(user.getId(), channel.getId(), Instant.now())
        );
        BinaryContent binary = repositories.binaries.create(
                new BinaryContent("image.png", "image/png", new byte[]{1, 2, 3})
        );
        Message message = repositories.messages.create(
                new Message(
                        "hello",
                        channel.getId(),
                        user.getId(),
                        List.of(binary.getId())
                )
        );

        assertEquals(user.getId(), repositories.users.findByUsername("woody").orElseThrow().getId());
        assertTrue(repositories.users.existsByUsername("woody"));
        assertTrue(repositories.users.existsByEmail("woody@example.com"));
        assertFalse(repositories.users.existsByUsername("missing"));
        assertFalse(repositories.users.existsByEmail("missing@example.com"));
        assertEquals(userStatus.getId(), repositories.userStatuses.findByUserId(user.getId()).orElseThrow().getId());
        assertEquals(
                List.of(readStatus.getId()),
                repositories.readStatuses.findAllByUserId(user.getId()).stream()
                        .map(ReadStatus::getId)
                        .toList()
        );
        assertEquals(
                List.of(readStatus.getId()),
                repositories.readStatuses.findAllByChannelId(channel.getId()).stream()
                        .map(ReadStatus::getId)
                        .toList()
        );
        assertEquals(readStatus.getId(), repositories.readStatuses
                .findByUserIdAndChannelId(user.getId(), channel.getId())
                .orElseThrow()
                .getId());
        assertEquals(
                List.of(message.getId()),
                repositories.messages.findAllByChannelId(channel.getId()).stream()
                        .map(Message::getId)
                        .toList()
        );
        assertEquals(message.getId(), repositories.messages
                .findLatestByChannelId(channel.getId())
                .orElseThrow()
                .getId());
        assertEquals(List.of(binary.getId()), repositories.binaries
                .findAllByIdIn(List.of(binary.getId()))
                .stream()
                .map(BinaryContent::getId)
                .toList());
        assertThrows(
                UnsupportedOperationException.class,
                () -> repositories.users.findAll().add(user)
        );

        byte[] loadedBytes = repositories.binaries.getById(binary.getId()).getBytes();
        loadedBytes[0] = 9;
        assertEquals(1, repositories.binaries.getById(binary.getId()).getBytes()[0]);
        assertThrows(
                EntityNotFoundException.class,
                () -> repositories.users.getById(UUID.randomUUID())
        );

        repositories.messages.deleteAllByChannelId(channel.getId());
        assertTrue(repositories.messages.findAllByChannelId(channel.getId()).isEmpty());

        repositories.readStatuses.deleteAllByUserId(user.getId());
        assertTrue(repositories.readStatuses.findAllByUserId(user.getId()).isEmpty());

        repositories.readStatuses.create(
                new ReadStatus(user.getId(), channel.getId(), Instant.now())
        );
        repositories.readStatuses.deleteAllByChannelId(channel.getId());
        assertTrue(repositories.readStatuses.findAllByChannelId(channel.getId()).isEmpty());

        repositories.userStatuses.deleteByUserId(user.getId());
        assertTrue(repositories.userStatuses.findByUserId(user.getId()).isEmpty());
    }

    private Repositories jcfRepositories() {
        return new Repositories(
                new JCFUserRepository(),
                new JCFChannelRepository(),
                new JCFMessageRepository(),
                new JCFReadStatusRepository(),
                new JCFUserStatusRepository(),
                new JCFBinaryContentRepository()
        );
    }

    private Repositories fileRepositories(Path root) {
        return new Repositories(
                new FileUserRepository(root),
                new FileChannelRepository(root),
                new FileMessageRepository(root),
                new FileReadStatusRepository(root),
                new FileUserStatusRepository(root),
                new FileBinaryContentRepository(root)
        );
    }

    private record Repositories(
            UserRepository users,
            ChannelRepository channels,
            MessageRepository messages,
            ReadStatusRepository readStatuses,
            UserStatusRepository userStatuses,
            BinaryContentRepository binaries
    ) {
    }
}
