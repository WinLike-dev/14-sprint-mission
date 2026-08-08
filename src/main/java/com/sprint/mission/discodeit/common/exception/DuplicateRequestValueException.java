package com.sprint.mission.discodeit.common.exception;

public class DuplicateRequestValueException extends DuplicateDataException {

    public DuplicateRequestValueException(Class<?> entityType, String fieldName) {
        super(
                "요청에 중복된 값이 있습니다. type=%s, field=%s"
                        .formatted(entityType.getSimpleName(), fieldName)
        );
    }
}
