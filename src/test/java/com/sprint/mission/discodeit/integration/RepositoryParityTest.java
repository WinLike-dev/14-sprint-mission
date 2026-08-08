package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.common.exception.StorageOperationException;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.binarycontent.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.message.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.userstatus.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.binarycontent.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.channel.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.message.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.userstatus.repository.jcf.JCFUserStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(userStatus.getId(), repositories.userStatuses.findByUserId(user.getId()).orElseThrow().getId());
        assertEquals(readStatus.getId(), repositories.readStatuses
                .findByUserIdAndChannelId(user.getId(), channel.getId())
                .orElseThrow()
                .getId());
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

        byte[] loadedBytes = repositories.binaries.findById(binary.getId()).getBytes();
        loadedBytes[0] = 9;
        assertEquals(1, repositories.binaries.findById(binary.getId()).getBytes()[0]);
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
