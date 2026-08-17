package com.sprint.mission.discodeit.common.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 요청한 엔티티를 저장소에서 찾을 수 없을 때 발생하는 예외.
 * NoSuchElementException을 상속하여, Java 표준 "요소 없음" 예외 계층에 속한다.
 * GlobalExceptionHandler에서 이 예외를 잡아 404 Not Found 응답으로 변환한다.
 */
public class EntityNotFoundException extends NoSuchElementException {

    // ID로 엔티티를 찾지 못했을 때 사용하는 생성자
    public EntityNotFoundException(Class<?> entityType, UUID id) {
        super(
                "엔티티를 찾을 수 없습니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }

    // ID가 아닌 다른 조건(예: 이름, 이메일 등)으로 찾지 못했을 때 사용하는 생성자
    public EntityNotFoundException(Class<?> entityType, String criteria) {
        super(
                "엔티티를 찾을 수 없습니다. type=%s, criteria=%s"
                        .formatted(entityType.getSimpleName(), criteria)
        );
    }
}
