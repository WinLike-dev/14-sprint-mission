package com.sprint.mission.discodeit.common.exception;

/**
 * 도메인 규칙을 만족하지 못하는 값이 들어왔을 때 사용하는 예외.
 *
 * 이전에는 도메인 검증도 그냥 IllegalArgumentException을 던졌다.
 * 그러면 "호출자가 잘못된 값을 보냈다"와 "코드에 버그가 있다"가 같은 타입이 되어,
 * 예외 처리기가 둘을 구분할 수 없고 내부 메시지가 그대로 응답에 실려 나간다.
 *
 * IllegalArgumentException을 상속해서 잘못된 인자라는 의미는 유지하되,
 * 전달할 수 있는 실패와 그렇지 않은 실패를 타입으로 구분한다.
 */
public class InvalidValueException extends IllegalArgumentException {

    public InvalidValueException(String message) {
        super(message);
    }

    // 필드 이름과 함께 표현할 때 사용한다.
    public static InvalidValueException blank(String fieldName) {
        return new InvalidValueException(fieldName + "은(는) 비어 있을 수 없습니다.");
    }
}
