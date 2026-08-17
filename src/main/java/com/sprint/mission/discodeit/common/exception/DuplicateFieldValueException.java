package com.sprint.mission.discodeit.common.exception;

/**
 * 엔티티의 특정 필드 값이 이미 존재할 때 발생하는 예외.
 * 예: 같은 이메일을 가진 사용자가 이미 있을 때, 같은 이름의 채널이 이미 있을 때 등.
 * DuplicateDataException을 상속하므로 GlobalExceptionHandler에서 409 Conflict로 처리된다.
 */
public class DuplicateFieldValueException extends DuplicateDataException {

    // entityType: 중복이 발생한 엔티티 클래스
    // fieldName: 중복된 필드 이름 (예: "email", "username")
    // value: 중복된 실제 값
    public DuplicateFieldValueException(Class<?> entityType, String fieldName, Object value) {
        super(
                "중복된 필드 값입니다. type=%s, field=%s, value=%s"
                        .formatted(entityType.getSimpleName(), fieldName, value)
        );
    }
}
