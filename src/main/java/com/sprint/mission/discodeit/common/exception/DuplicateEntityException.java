package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

/**
 * 동일한 ID를 가진 엔티티가 이미 저장소에 존재할 때 발생하는 예외.
 * 같은 엔티티를 중복 저장하려는 시도를 방지한다.
 * DuplicateDataException을 상속하므로 GlobalExceptionHandler에서 409 Conflict로 처리된다.
 */
public class DuplicateEntityException extends DuplicateDataException {

    // entityType: 중복된 엔티티의 클래스
    // id: 이미 존재하는 엔티티의 UUID
    public DuplicateEntityException(Class<?> entityType, UUID id) {
        super(
                "이미 존재하는 엔티티입니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }
}
