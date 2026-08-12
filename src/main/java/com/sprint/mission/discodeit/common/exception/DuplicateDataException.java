package com.sprint.mission.discodeit.common.exception;

public abstract class DuplicateDataException extends RuntimeException {

    protected DuplicateDataException(String message) {
        super(message);
    }
}
