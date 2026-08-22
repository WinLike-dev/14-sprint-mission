package com.sprint.mission.discodeit.common.exception;

/**
 * 이미 저장된 데이터와 충돌해서 요청을 처리할 수 없을 때 사용하는 예외.
 *
 * 이 계열은 "지금 저장소에 그 값이 있다"는 사실 때문에 실패한다.
 * 같은 요청이라도 기존 데이터가 달라지면 성공할 수 있다.
 *
 * 요청 안에서만 발생하는 중복(DuplicateRequestValueException)이나
 * 식별자 생성 결함(DuplicateEntityException)은 성격이 달라 이 계열에 두지 않는다.
 * 이름에 Duplicate가 들어간다고 같은 상태로 응답하지는 않는다.
 */
public abstract class DuplicateDataException extends RuntimeException {

    protected DuplicateDataException(String message) {
        super(message);
    }
}
