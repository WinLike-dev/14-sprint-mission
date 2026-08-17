package com.sprint.mission.discodeit.common.exception;

/**
 * 요청 자체는 올바르지만 대상의 현재 상태 때문에 처리할 수 없을 때 사용하는 예외.
 *
 * 예를 들어 비공개 채널을 수정하려는 요청은 문법적으로 완전하다.
 * 거부되는 이유는 요청이 아니라 그 채널이 지금 PRIVATE이라는 상태에 있다.
 * 이런 실패를 400으로 답하면 "요청을 이해할 수 없다"는 뜻이 되어 원인을 잘못 가리킨다.
 *
 * 같은 요청이라도 대상의 상태가 달라지면 성공할 수 있다는 점이 400과의 차이다.
 */
public abstract class ConflictingStateException extends RuntimeException {

    protected ConflictingStateException(String message) {
        super(message);
    }
}
