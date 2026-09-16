package com.sprint.mission.discodeit.content.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    // 파일을 저장하고, 트랜잭션이 롤백되면 저장한 파일을 지운다.
    public void save(UUID binaryContentId, byte[] bytes) {
        storage.put(binaryContentId, bytes);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    deleteQuietly(binaryContentId);
                }
            }
        });
    }

    // 파일을 커밋된 뒤에 지운다.
    // 먼저 지웠다가 롤백되면 "행은 있는데 파일은 없는" 상태가 되기 때문이다.
    public void deleteAfterCommit(UUID binaryContentId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteQuietly(binaryContentId);
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
