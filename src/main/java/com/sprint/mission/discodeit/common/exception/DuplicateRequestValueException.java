package com.sprint.mission.discodeit.common.exception;

/**
 * 요청 하나 안에 같은 값이 중복해서 들어왔을 때 발생한다.
 *
 * 예전에는 DuplicateDataException을 상속해서 409로 나갔다. 하지만 409는 서버의 현재
 * 상태와 충돌한다는 뜻이고, 이 실패는 저장된 데이터와 아무 관련이 없다.
 * 참여자 목록에 같은 사용자를 두 번 넣은 요청은 서버 상태가 어떻든 항상 거부된다.
 * 그래서 잘못된 요청 값(400)으로 분류한다.
 */
public class DuplicateRequestValueException extends InvalidValueException {

    public DuplicateRequestValueException(Class<?> entityType, String fieldName) {
        super(
                "요청에 중복된 값이 있습니다. type=%s, field=%s"
                        .formatted(entityType.getSimpleName(), fieldName)
        );
    }
}
