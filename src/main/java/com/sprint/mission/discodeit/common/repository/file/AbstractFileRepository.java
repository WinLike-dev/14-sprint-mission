package com.sprint.mission.discodeit.common.repository.file;

import com.sprint.mission.discodeit.common.entity.Identifiable;
import com.sprint.mission.discodeit.common.exception.StorageOperationException;
import com.sprint.mission.discodeit.common.repository.AbstractCrudRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractFileRepository<T extends Identifiable & Serializable>
        extends AbstractCrudRepository<T> {

    private static final String FILE_EXTENSION = ".ser";
    private final Path directory;
    private final Class<T> entityType;

    protected AbstractFileRepository(Path directory, Class<T> entityType) {
        super(entityType);
        this.directory = Objects.requireNonNull(directory);
        this.entityType = Objects.requireNonNull(entityType);
    }

    @Override
    protected final void write(T entity) {
        UUID id = entity.getId();
        Path temporaryFile = null;
        Stage stage = Stage.CREATE_TEMP_FILE;

        try {
            temporaryFile = createTemporaryFile(id);
            stage = Stage.SERIALIZE;
            serialize(entity, temporaryFile);
            stage = Stage.MOVE_FILE;
            replaceStoredFile(temporaryFile, id);
        } catch (IOException exception) {
            cleanup(temporaryFile, exception);
            throw repositoryException(Operation.WRITE, stage, id, exception);
        }
    }

    @Override
    protected final Optional<T> read(UUID id) {
        try {
            return Optional.of(deserialize(filePath(id), id));
        } catch (NoSuchFileException exception) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException | ClassCastException exception) {
            throw repositoryException(Operation.READ, Stage.DESERIALIZE, id, exception);
        }
    }

    @Override
    protected final List<T> readAll() {
        List<T> entities = new ArrayList<>();
        Stage stage = Stage.OPEN_DIRECTORY;

        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(directory, "*" + FILE_EXTENSION)) {
            stage = Stage.ITERATE_DIRECTORY;
            for (Path filePath : filePaths) {
                readStoredEntity(filePath).ifPresent(entities::add);
            }
        } catch (NoSuchFileException exception) {
            return entities;
        } catch (IOException | DirectoryIteratorException exception) {
            throw repositoryException(Operation.READ_ALL, stage, null, exception);
        }

        return entities;
    }

    @Override
    protected final void remove(UUID id) {
        try {
            Files.deleteIfExists(filePath(id));
        } catch (IOException exception) {
            throw repositoryException(Operation.REMOVE, Stage.DELETE_FILE, id, exception);
        }
    }

    private Optional<T> readStoredEntity(Path filePath) {
        UUID expectedId = null;
        Stage stage = Stage.PARSE_FILE_NAME;

        try {
            expectedId = idFromFileName(filePath);
            stage = Stage.DESERIALIZE;
            return Optional.of(deserialize(filePath, expectedId));
        } catch (NoSuchFileException exception) {
            return Optional.empty();
        } catch (IOException | ClassNotFoundException | ClassCastException exception) {
            throw repositoryException(
                    Operation.READ_ALL,
                    stage,
                    expectedId,
                    exception
            );
        }
    }

    private Path createTemporaryFile(UUID id) throws IOException {
        Files.createDirectories(directory);
        return Files.createTempFile(directory, "." + id + "-", ".tmp");
    }

    private void serialize(T entity, Path temporaryFile) throws IOException {
        try (OutputStream fileOutputStream = Files.newOutputStream(temporaryFile);
             ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(entity);
        }
    }

    private void replaceStoredFile(Path temporaryFile, UUID id) throws IOException {
        Files.move(
                temporaryFile,
                filePath(id),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private T deserialize(Path filePath, UUID expectedId)
            throws IOException, ClassNotFoundException {
        try (InputStream fileInputStream = Files.newInputStream(filePath);
             ObjectInputStream objectInputStream =
                     new ObjectInputStream(fileInputStream)) {
            Object object = objectInputStream.readObject();

            if (object == null) {
                throw invalidObjectException(filePath, "역직렬화 결과가 null입니다.");
            }

            T entity = entityType.cast(object);
            if (!expectedId.equals(entity.getId())) {
                throw invalidObjectException(
                        filePath,
                        "expectedId=%s, actualId=%s"
                                .formatted(expectedId, entity.getId())
                );
            }

            return entity;
        }
    }

    private UUID idFromFileName(Path filePath) throws InvalidObjectException {
        String fileName = filePath.getFileName().toString();
        String id = fileName.substring(
                0,
                fileName.length() - FILE_EXTENSION.length()
        );

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            InvalidObjectException invalidObjectException = invalidObjectException(
                    filePath,
                    "파일명이 UUID 형식이 아닙니다."
            );
            invalidObjectException.initCause(exception);
            throw invalidObjectException;
        }
    }

    private InvalidObjectException invalidObjectException(
            Path filePath,
            String reason
    ) {
        return new InvalidObjectException(
                "저장 객체가 올바르지 않습니다. path=%s, reason=%s"
                        .formatted(filePath, reason)
        );
    }

    private void cleanup(Path temporaryFile, IOException originalException) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }

    private Path filePath(UUID id) {
        return directory.resolve(id + FILE_EXTENSION);
    }

    private StorageOperationException repositoryException(
            Operation operation,
            Stage stage,
            UUID id,
            Throwable cause
    ) {
        return new StorageOperationException(
                operation.value,
                stage.description,
                entityType,
                id,
                cause
        );
    }

    private enum Operation {
        WRITE("write"),
        READ("read"),
        READ_ALL("readAll"),
        REMOVE("remove");

        private final String value;

        Operation(String value) {
            this.value = value;
        }
    }

    private enum Stage {
        CREATE_TEMP_FILE("임시 파일 생성"),
        SERIALIZE("직렬화"),
        MOVE_FILE("파일 이동"),
        OPEN_DIRECTORY("저장 디렉터리 열기"),
        ITERATE_DIRECTORY("저장 파일 목록 순회"),
        PARSE_FILE_NAME("파일명에서 식별자 추출"),
        DESERIALIZE("역직렬화"),
        DELETE_FILE("저장 파일 삭제");

        private final String description;

        Stage(String description) {
            this.description = description;
        }
    }
}
