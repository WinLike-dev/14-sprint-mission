package com.sprint.mission.discodeit.common.exception;

/**
 * 클라이언트 요청(Request) 내에 중복된 값이 포함되어 있을 때 발생하는 예외.
 * 예: 요청 본문에 같은 필드가 두 번 전달되거나, 목록에 중복 항목이 있을 때 등.
 * DuplicateDataException을 상속하므로 GlobalExceptionHandler에서 409 Conflict로 처리된다.
 */
public class DuplicateRequestValueException extends DuplicateDataException {

    // entityType: 요청 대상이 되는 엔티티 클래스
    // fieldName: 중복된 값이 있는 필드 이름
    public DuplicateRequestValueException(Class<?> entityType, String fieldName) {
        super(
                "요청에 중복된 값이 있습니다. type=%s, field=%s"
                        .formatted(entityType.getSimpleName(), fieldName)
        );
    }
}
