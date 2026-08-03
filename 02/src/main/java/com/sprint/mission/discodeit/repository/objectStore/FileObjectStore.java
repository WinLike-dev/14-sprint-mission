package com.sprint.mission.discodeit.repository.objectStore;

import com.sprint.mission.discodeit.entity.Identifiable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FileObjectStore<T extends Identifiable & Serializable>
        implements ObjectStore<T> {

    private static final String FILE_EXTENSION = ".ser";

    private final Path directory;
    private final Class<T> entityType;

    public FileObjectStore(
            Path directory,
            Class<T> entityType
    ) {
        this.directory = Objects.requireNonNull(directory);
        this.entityType = Objects.requireNonNull(entityType);
    }

    @Override
    public void write(T entity) throws IOException {
        UUID id = entity.getId();
        Path temporaryFile = null;

        try {
            temporaryFile = createTemporaryFile(id);
            serialize(entity, temporaryFile);
            replaceStoredFile(temporaryFile, id);
        } catch (IOException exception) {
            cleanup(temporaryFile, exception);
            throw exception;
        }
    }

    @Override
    public Optional<T> read(UUID id)
            throws IOException, ClassNotFoundException {
        return Optional.of(deserialize(filePath(id), id));
    }

    @Override
    public List<T> read() throws IOException, ClassNotFoundException {
        List<T> entities = new ArrayList<>();

        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(
                             directory,
                             "*" + FILE_EXTENSION
                     )) {
            for (Path filePath : filePaths) {
                UUID expectedId = idFromFileName(filePath);
                entities.add(deserialize(filePath, expectedId));
            }
        }

        return entities;
    }

    @Override
    public void remove(UUID id) throws IOException {
        Files.delete(filePath(id));
    }

    private void cleanup(
            Path temporaryFile,
            IOException originalException
    ) {
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

    private Path createTemporaryFile(UUID id) throws IOException {
        Files.createDirectories(directory);
        return Files.createTempFile(
                directory,
                "." + id + "-",
                ".tmp"
        );
    }

    private void serialize(
            T entity,
            Path temporaryFile
    ) throws IOException {
        try (OutputStream fileOutputStream =
                     Files.newOutputStream(temporaryFile);
             ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(entity);
        }
    }

    private void replaceStoredFile(
            Path temporaryFile,
            UUID id
    ) throws IOException {
        Files.move(
                temporaryFile,
                filePath(id),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private T deserialize(
            Path filePath,
            UUID expectedId
    ) throws IOException, ClassNotFoundException {
        try (InputStream fileInputStream =
                     Files.newInputStream(filePath);
             ObjectInputStream objectInputStream =
                     new ObjectInputStream(fileInputStream)) {
            Object object = objectInputStream.readObject();

            if (object == null) {
                throw invalidObjectException(
                        filePath,
                        "역직렬화 결과가 null입니다."
                );
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

    private UUID idFromFileName(Path filePath)
            throws InvalidObjectException {
        String fileName = filePath.getFileName().toString();
        String id = fileName.substring(
                0,
                fileName.length() - FILE_EXTENSION.length()
        );

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            InvalidObjectException invalidObjectException =
                    invalidObjectException(
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
}
