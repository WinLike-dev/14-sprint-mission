package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.repository.objectStore.StorageOperation;
import com.sprint.mission.discodeit.repository.objectStore.StorageStage;
import lombok.Getter;

import java.util.UUID;

// 스토리지 예외는 catch 없이 상위라인에 계속 전파하려고 런타임 상속
@Getter
public class StorageOperationException extends RuntimeException {

    public StorageOperationException(
            StorageOperation operation,
            StorageStage stage,
            Class<?> entityType,
            UUID id,
            Throwable cause
    ) {
        super(
                "저장소 작업에 실패했습니다. operation=%s, stage=%s, %s"
                        .formatted(
                                operation.getValue(),
                                stage.getDescription(),
                                dataContext(entityType, id)),
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
