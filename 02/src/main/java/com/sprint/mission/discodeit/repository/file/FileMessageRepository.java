package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.CrudRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements CrudRepository<Message> {

    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;

    public FileMessageRepository() {
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
    public Message create(Message message) {
        Path filePath =
                directory.resolve(message.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.CREATE_NEW,
                                     StandardOpenOption.WRITE
                             )
                     )) {
            outputStream.writeObject(message);
            return message;
        } catch (FileAlreadyExistsException exception) {
            throw new IllegalStateException(
                    "이미 존재하는 메시지입니다: " + message.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 저장에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public Message findById(UUID id) {
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
                    "메시지 조회에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<Message> findAll() {
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
                    "전체 메시지 조회에 실패했습니다.",
                    exception
            );
        }

        return messages;
    }

    @Override
    public Message update(Message message) {
        Path filePath =
                directory.resolve(message.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(message);
            return message;
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 메시지 파일을 찾을 수 없습니다: "
                            + message.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "메시지 수정 결과 저장에 실패했습니다: "
                            + message.getId(),
                    exception
            );
        }
    }

    @Override
    public void deleteById(UUID id) {
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
                    "메시지 삭제에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
