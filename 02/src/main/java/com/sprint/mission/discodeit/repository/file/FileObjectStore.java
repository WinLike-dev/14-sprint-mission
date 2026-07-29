package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// 설계: 파일 저장 기술만 책임지고 도메인 Repository나 비즈니스 규칙은 알지 못한다.
public final class FileObjectStore<T extends Identifiable & Serializable> {

    private static final String FILE_EXTENSION = ".ser";

    private final Path directory;
    private final Class<T> type;
    private final String entityName;

    public FileObjectStore(
            Path directory,
            Class<T> type,
            String entityName
    ) {
        this.directory = Objects.requireNonNull(directory);
        this.type = Objects.requireNonNull(type);
        this.entityName = Objects.requireNonNull(entityName);

        try {
            Files.createDirectories(directory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    entityName + " 저장 디렉터리 준비에 실패했습니다.",
                    exception
            );
        }
    }

    public T create(T entity) {
        Path filePath =
                directory.resolve(entity.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.CREATE_NEW,
                                     StandardOpenOption.WRITE
                             )
                     )) {
            outputStream.writeObject(entity);
            return entity;
        } catch (FileAlreadyExistsException exception) {
            throw new IllegalStateException(
                    "이미 존재하는 " + entityName + "입니다: "
                            + entity.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    entityName + " 저장에 실패했습니다.",
                    exception
            );
        }
    }

    public T findById(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try {
            return deserialize(filePath);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    entityName + " 파일을 찾을 수 없습니다: " + id,
                    exception
            );
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    entityName + " 조회에 실패했습니다: " + id,
                    exception
            );
        }
    }

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();

        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(
                             directory,
                             "*" + FILE_EXTENSION
                     )) {
            for (Path filePath : filePaths) {
                entities.add(deserialize(filePath));
            }
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw new IllegalStateException(
                    "전체 " + entityName + " 조회에 실패했습니다.",
                    exception
            );
        }

        return entities;
    }

    public T update(T entity) {
        Path filePath =
                directory.resolve(entity.getId() + FILE_EXTENSION);

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             Files.newOutputStream(
                                     filePath,
                                     StandardOpenOption.WRITE,
                                     StandardOpenOption.TRUNCATE_EXISTING
                             )
                     )) {
            outputStream.writeObject(entity);
            return entity;
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "수정할 " + entityName + " 파일을 찾을 수 없습니다: "
                            + entity.getId(),
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    entityName + " 수정 결과 저장에 실패했습니다: "
                            + entity.getId(),
                    exception
            );
        }
    }

    public void deleteById(UUID id) {
        Path filePath = directory.resolve(id + FILE_EXTENSION);

        try {
            Files.delete(filePath);
        } catch (NoSuchFileException exception) {
            throw new IllegalStateException(
                    "삭제할 " + entityName + " 파일을 찾을 수 없습니다: "
                            + id,
                    exception
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    entityName + " 삭제에 실패했습니다: " + id,
                    exception
            );
        }
    }

    private T deserialize(
            Path filePath
    ) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(
                             Files.newInputStream(filePath)
                     )) {
            Object object = inputStream.readObject();
            return type.cast(object);
        }
    }
}
