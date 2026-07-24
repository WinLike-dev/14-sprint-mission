package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
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
import java.util.UUID;

public class FileUserService implements UserService {

    // 문법: static final 상수는 모든 인스턴스가 같은 파일 확장자 값을 공유하게 한다.
    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;

    public FileUserService() {
        directory = Path.of("data", "users");

        // 문법: 파일 API의 checked exception은 실제 작업이 일어나는 메서드에서 처리한다.
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
    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
        Path filePath =
                directory.resolve(user.getId() + FILE_EXTENSION);

        // 문법: try-with-resources는 작업이 끝나면 스트림을 자동으로 닫는다.
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(filePath)
                     )) {
            outputStream.writeObject(user);
            return user;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 생성 중 파일 처리에 실패했습니다.",
                    exception
            );
        }
    }

    @Override
    public User readUser(UUID id) {
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
                    "사용자 조회 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public List<User> readAllUsers() {
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
                    "전체 사용자 조회 중 파일 처리에 실패했습니다.",
                    exception
            );
        }

        // 문법: List.copyOf는 호출자가 결과 목록을 수정하지 못하게 한다.
        return List.copyOf(users);
    }

    @Override
    public void updateUser(UUID id, String username, String email, String password) {
        User user = readUser(id);
        user.update(username, email, password);
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(user);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 사용자 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "사용자 수정 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }

    @Override
    public void deleteUser(UUID id) {
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
                    "사용자 삭제 중 파일 처리에 실패했습니다: " + id,
                    exception
            );
        }
    }
}
