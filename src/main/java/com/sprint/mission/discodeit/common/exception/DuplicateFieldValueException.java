package com.sprint.mission.discodeit.common.exception;

public class DuplicateFieldValueException extends DuplicateDataException {

    public DuplicateFieldValueException(Class<?> entityType, String fieldName, Object value) {
        super(
                "중복된 필드 값입니다. type=%s, field=%s, value=%s"
                        .formatted(entityType.getSimpleName(), fieldName, value)
        );
    }
}
