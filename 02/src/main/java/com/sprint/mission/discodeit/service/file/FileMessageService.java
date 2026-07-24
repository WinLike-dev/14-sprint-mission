package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private static final String FILE_EXTENSION = ".ser";

    // 설계: 메시지의 연관 관계 검증을 위해 서비스 계약에 의존하고 구체 구현체는 알지 않는다.
    private final UserService userService;
    private final ChannelService channelService;
    private final Path directory;

    public FileMessageService(
            UserService userService,
            ChannelService channelService
    ) {
        this.userService = Objects.requireNonNull(userService);
        this.channelService = Objects.requireNonNull(channelService);
        directory = Path.of("data", "messages");

        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 저장 디렉터리 준비에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Message createMessage(
            String content,
            UUID channelId,
        UUID senderId,
        UUID receiverId
    ) {
        try {
            channelService.readChannel(channelId);
            userService.readUser(senderId);
            userService.readUser(receiverId);
        } catch (NoSuchElementException exception) {
            throw new IllegalStateException(
                    "메시지 연관 데이터를 찾을 수 없습니다.",
                    exception
            );
        }

        Message message =
                new Message(content, channelId, senderId, receiverId);
        Path filePath =
                directory.resolve(message.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(filePath)
                     )) {
            outputStream.writeObject(message);
            return message;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 생성 중 파일 처리에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Message readMessage(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             Files.newInputStream(filePath)
                     )) {
            return (Message) inputStream.readObject();
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "메시지 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "메시지 조회 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<Message> readAllMessages() {
        List<Message> messages = new ArrayList<>();

        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(
                             directory,
                             "*" + FILE_EXTENSION
                     )) {
            for (Path filePath : filePaths) {
                try (ObjectInputStream inputStream =
                             new ObjectInputStream(
                                     Files.newInputStream(filePath)
                             )) {
                    messages.add((Message) inputStream.readObject());
                }
            }
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "전체 메시지 조회 중 파일 처리에 실패했습니다.",
                    exception
            );
        }

        return List.copyOf(messages);
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = readMessage(id);
        message.update(content);
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(message);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 메시지 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 수정 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public void deleteMessage(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try {
            Files.delete(filePath);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "삭제할 메시지 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 삭제 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
