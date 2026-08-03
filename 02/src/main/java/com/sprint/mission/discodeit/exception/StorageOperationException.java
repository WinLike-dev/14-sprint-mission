package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class StorageOperationException extends RuntimeException {

    public StorageOperationException(
            String operation,
            Class<?> entityType,
            UUID id,
            Throwable cause
    ) {
        super(
                "저장소 작업에 실패했습니다. operation=%s, %s"
                        .formatted(operation, dataContext(entityType, id)),
                cause
        );
    }

    private static String dataContext(Class<?> entityType, UUID id) {
        if (id == null) {
            return "type=" + entityType.getSimpleName();
        }
        return "type=%s, id=%s".formatted(
                entityType.getSimpleName(),
                id
        );
    }
}
