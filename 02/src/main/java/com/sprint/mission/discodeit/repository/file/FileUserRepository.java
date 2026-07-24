package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
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

public class FileUserRepository implements CrudRepository<User> {

    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;

    public FileUserRepository() {
        directory = Path.of("data", "users");

        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 저장 디렉터리 준비에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public User create(User user) {
        Path filePath =
                directory.resolve(user.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.CREATE_NEW,
                                     StandardOpenOption.WRITE
                             )
                     )) {
            outputStream.writeObject(user);
            return user;
        } catch (FileAlreadyExistsException exception) {
            throw new IllegalStateException(
                    "이미 존재하는 사용자입니다: " + user.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 저장에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public User findById(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             Files.newInputStream(filePath)
                     )) {
            return (User) inputStream.readObject();
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "사용자 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "사용자 조회에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

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
                    users.add((User) inputStream.readObject());
                }
            }
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "전체 사용자 조회에 실패했습니다.",
                    exception
            );
        }

        return users;
    }

    @Override
    public User update(User user) {
        Path filePath =
                directory.resolve(user.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(user);
            return user;
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 사용자 파일을 찾을 수 없습니다: "
                            + user.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 수정 결과 저장에 실패했습니다: "
                            + user.getId(),
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
                    "삭제할 사용자 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 삭제에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
