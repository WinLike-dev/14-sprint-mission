package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class EntityNotFoundException extends NoSuchElementException {

    public EntityNotFoundException(Class<?> entityType, UUID id) {
        super(
                "엔티티를 찾을 수 없습니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }
}
