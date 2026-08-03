package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(Class<?> entityType, UUID id) {
        super(
                "이미 존재하는 엔티티입니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }
}
