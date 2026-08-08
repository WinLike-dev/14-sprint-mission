package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

public class DuplicateEntityException extends DuplicateDataException {

    public DuplicateEntityException(Class<?> entityType, UUID id) {
        super(
                "이미 존재하는 엔티티입니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }
}
