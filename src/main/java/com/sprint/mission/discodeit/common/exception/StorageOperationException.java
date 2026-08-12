package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

// 저장 기술 경계의 실패를 호출자가 도메인 부재와 구분할 수 있게 하는 이름 있는 예외다.
public class StorageOperationException extends RuntimeException {

    public StorageOperationException(
            String operation,
            String stage,
            Class<?> entityType,
            UUID id,
            Throwable cause
    ) {
        super(
                "저장 작업에 실패했습니다. operation=%s, stage=%s, %s"
                        .formatted(operation, stage, dataContext(entityType, id)),
                cause
        );
    }

    private static String dataContext(Class<?> entityType, UUID id) {
        return id == null
                ? "type=" + entityType.getSimpleName()
                : "type=%s, id=%s".formatted(entityType.getSimpleName(), id);
    }
}
