package com.sprint.mission.discodeit.content.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 트랜잭션 결과에 맞춰 바이너리 파일을 저장·삭제한다.
 * DB 행은 롤백되어도 디스크 파일은 되돌아가지 않기 때문에, 파일 작업 시점을 트랜잭션에 맞춘다.
 * TransactionSynchronization을 등록하므로 @Transactional 메서드 안에서만 호출할 수 있다.
 */
@Component
@RequiredArgsConstructor
public class BinaryContentFileManager {

    private static final Logger log = LoggerFactory.getLogger(BinaryContentFileManager.class);

    private final BinaryContentStorage storage;

    /**
     * 저장할 파일 하나. BinaryContent의 id와 그 내용을 짝지어 전달한다.
     */
    public record FileToSave(UUID binaryContentId, byte[] bytes) {

        public FileToSave {
            Objects.requireNonNull(binaryContentId, "binaryContentId는 null일 수 없습니다.");
            Objects.requireNonNull(bytes, "bytes는 null일 수 없습니다.");
        }
    }

    // 여러 파일을 저장하고, 트랜잭션이 롤백되면 저장한 파일을 모두 지운다.
    // 중간에 실패해도 그때까지 저장한 파일이 정리되도록 콜백을 먼저 등록한다.
    public void saveAll(List<FileToSave> files) {
        List<FileToSave> targets = List.copyOf(files);
        if (targets.isEmpty()) {
            return;
        }
        List<UUID> saved = new ArrayList<>();
        registerRollbackCleanup(saved);
        for (FileToSave file : targets) {
            storage.put(file.binaryContentId(), file.bytes());
            saved.add(file.binaryContentId());
        }
    }

    // 파일 하나를 저장한다. 프로필처럼 파일이 하나뿐인 경우를 위한 편의 메서드다.
    public void save(UUID binaryContentId, byte[] bytes) {
        saveAll(List.of(new FileToSave(binaryContentId, bytes)));
    }

    // 여러 파일을 커밋된 뒤에 지운다. 콜백은 한 번만 등록한다.
    // 먼저 지웠다가 롤백되면 "행은 있는데 파일은 없는" 상태가 되기 때문이다.
    public void deleteAllAfterCommit(Collection<UUID> binaryContentIds) {
        List<UUID> targets = List.copyOf(binaryContentIds);
        if (targets.isEmpty()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                targets.forEach(BinaryContentFileManager.this::deleteQuietly);
            }
        });
    }

    // 파일 하나를 커밋된 뒤에 지운다.
    public void deleteAfterCommit(UUID binaryContentId) {
        deleteAllAfterCommit(List.of(binaryContentId));
    }

    // 롤백되면 그때까지 저장한 파일을 지운다. saved는 저장이 진행되며 채워진다.
    private void registerRollbackCleanup(List<UUID> saved) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    saved.forEach(BinaryContentFileManager.this::deleteQuietly);
                }
            }
        });
    }

    // 트랜잭션이 끝난 뒤의 삭제 실패는 요청을 실패시키지 않는다. 최악은 디스크에 고아 파일이 남는 것이다.
    private void deleteQuietly(UUID binaryContentId) {
        try {
            storage.delete(binaryContentId);
        } catch (RuntimeException exception) {
            log.warn("파일을 삭제하지 못했습니다. id={}", binaryContentId, exception);
        }
    }
}
