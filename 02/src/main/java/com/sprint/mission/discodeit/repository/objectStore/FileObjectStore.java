package com.sprint.mission.discodeit.repository.objectStore;

import com.sprint.mission.discodeit.entity.Identifiable;
import com.sprint.mission.discodeit.exception.StorageOperationException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileObjectStore<T extends Identifiable & Serializable>
        implements ObjectStore<T> {

    private static final String FILE_EXTENSION = ".ser";

    private final Path directory;
    private final Class<T> entityType;


    private static final Logger log =
            LoggerFactory.getLogger(FileObjectStore.class);

    public FileObjectStore(
            Path directory,
            Class<T> entityType
    ) {
        this.directory = Objects.requireNonNull(directory);
        this.entityType = Objects.requireNonNull(entityType);
    }

    // CRUD 예외 처리 목록 private에서 각각 catch하지 말고 한 번에 모아서
    // 오퍼레이션 쪽에서만 처리하기 대신 구체적인 정보를 operation과 stage, cause로 처리
    @Override
    public void save(T entity) {
        UUID id = entity.getId();
        StorageOperation operation = StorageOperation.SAVE;
        Path temporaryFile = null;

        StorageStage stage = StorageStage.CREATE_TEMP_FILE;
        try {
            temporaryFile = createTemporaryFile(id);

            stage = StorageStage.SERIALIZE;
            serialize(entity, temporaryFile);

            stage = StorageStage.MOVE_FILE;
            replaceStoredFile(temporaryFile, id);
        } catch (IOException exception) {
            // 현재 문제를 함께 넣어줘서 복구 로직에도 함께 넣기
            cleanup(temporaryFile, exception);
            // cleanup 실패 시 함께 넣는 로직도 같이
            throw storageException(
                    operation,
                    stage,
                    id,
                    exception);
        }
    }

    @Override
    public Optional<T> load(UUID id) {
        StorageOperation operation = StorageOperation.LOAD;
        StorageStage stage = StorageStage.DESERIALIZE;

        try {
            return Optional.of(deserialize(filePath(id), id));
        } catch (NoSuchFileException exception) {
            return Optional.empty();
        } catch (IOException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            throw storageException(
                    operation,
                    stage,
                    id,
                    exception);
        }
    }

    @Override
    public List<T> load() {
        List<T> entities = new ArrayList<>();
        StorageOperation operation = StorageOperation.LOAD_ALL;
        StorageStage stage = StorageStage.OPEN_DIRECTORY;


        try (DirectoryStream<Path> filePaths =
                     Files.newDirectoryStream(
                             directory,
                             "*" + FILE_EXTENSION
                     )) {
            stage = StorageStage.ITERATE_DIRECTORY;

            for (Path filePath : filePaths) {
                // 감당 가능한 개별 파일 오류 -> null 체크 후 저장
                // 감당 불가능한 것 -> storageException 런타임 예외 처리
                Optional<T> storedEntity = loadStoredEntity(filePath);
                storedEntity.ifPresent(entities::add);
            }
            // (1) 디렉토리 없을 때 문제
        } catch (NoSuchFileException exception) {
            return new ArrayList<>();
            // (2) 예측 불가능한 오류
            // (3) 디렉토리 순회 오류 둘 모두 런타임 예외 처리
        } catch (IOException |
                 DirectoryIteratorException exception) {
            throw storageException(
                    operation,
                    stage,
                    null,
                    exception
            );
        }

        return entities;
    }

    @Override
    public void delete(UUID id) {
        StorageOperation operation = StorageOperation.DELETE;
        try {
            Files.deleteIfExists(filePath(id));
        } catch (IOException exception) {
            throw storageException(
                    operation,
                    StorageStage.DELETE_FILE,
                    id,
                    exception
            );
        }
    }

    private Optional<T> loadStoredEntity(Path filePath) {
        UUID expectedId = null;
        StorageOperation operation = StorageOperation.LOAD_ALL;
        StorageStage stage = StorageStage.PARSE_FILE_NAME;

        try {
            // 외부 원인으로 파일 손상 체크
            expectedId = idFromFileName(filePath);
            stage = StorageStage.DESERIALIZE;
            return Optional.of(deserialize(filePath, expectedId));

            // 이 정도의 예외는 무시하고 갈 수 있다 처리
        } catch (NoSuchFileException |
                 EOFException |
                 ObjectStreamException |
                 ClassNotFoundException |
                 ClassCastException exception) {
            log.warn(
                    "잘못된 저장 파일을 제외합니다. stage={}, "
                            + "filePath={}, cause={}",
                    stage.getDescription(),
                    filePath,
                    exception.toString()
            );
            return Optional.empty();
        } catch (IOException exception) {
            throw storageException(
                    operation,
                    stage,
                    expectedId,
                    exception
            );
        }
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
            // 정리 과정에서 발생한 예외를 원본 예외의 보조 원인으로 추가
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
    ) throws IOException,
            ClassNotFoundException {
        try (InputStream fileInputStream =
                     Files.newInputStream(filePath);
             ObjectInputStream objectInputStream =
                     new ObjectInputStream(fileInputStream)) {
            Object object = objectInputStream.readObject();

            // 역질렬 결과 null
            if (object == null) {
                throw invalidObjectException(
                        filePath,
                        "역직렬화 결과가 null입니다."
                );
            }

            T entity = entityType.cast(object);

            // 예상 id (파일 추출)와 기존 id 동일 확인
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
        // fileName 만 가져오기
        String id = fileName.substring(
                0,
                fileName.length() - FILE_EXTENSION.length()
        );

        try {
            // (UUID형식인) string 값을 UUID로 변환 못하면 불일치로 간주
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            // new를 사용하지 않은 정적 팩토리 매서드
            InvalidObjectException invalidObjectException =
                    invalidObjectException(
                            filePath,
                            "파일명이 UUID 형식이 아닙니다."
                    );
            // 근원 원인 탑재
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

    private StorageOperationException storageException(
            StorageOperation operation,
            StorageStage stage,
            UUID id,
            Throwable cause
    ) {
        return new StorageOperationException(
                operation,
                stage,
                entityType,
                id,
                cause
        );
    }
}
