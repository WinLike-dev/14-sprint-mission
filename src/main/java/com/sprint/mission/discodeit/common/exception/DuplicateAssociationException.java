package com.sprint.mission.discodeit.common.exception;

public class DuplicateAssociationException extends DuplicateDataException {

    public DuplicateAssociationException(Class<?> entityType, String association) {
        super(
                "중복된 연관 관계입니다. type=%s, association=%s"
                        .formatted(entityType.getSimpleName(), association)
        );
    }
}
