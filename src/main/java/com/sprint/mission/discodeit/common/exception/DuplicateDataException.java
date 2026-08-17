package com.sprint.mission.discodeit.common.exception;

/**
 * 데이터 중복과 관련된 모든 예외의 부모(추상) 클래스.
 * 이 클래스를 직접 사용하지 않고, 구체적인 하위 클래스를 통해 중복의 종류를 구분한다.
 * 예: 필드 값 중복(DuplicateFieldValueException), 엔티티 중복(DuplicateEntityException) 등.
 *
 * 추상 클래스로 만든 이유: 중복 예외들을 하나의 타입으로 묶어서
 * GlobalExceptionHandler에서 한꺼번에 처리(409 Conflict)할 수 있게 하기 위함이다.
 */
public abstract class DuplicateDataException extends RuntimeException {

    protected DuplicateDataException(String message) {
        super(message);
    }
}
