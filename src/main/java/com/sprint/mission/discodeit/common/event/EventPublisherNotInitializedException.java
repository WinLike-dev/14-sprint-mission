package com.sprint.mission.discodeit.common.event;

/**
 * Events.raise()를 호출했지만, 아직 이벤트 발행기(publisher)가 초기화되지 않았을 때 발생하는 예외.
 * 보통 Spring 컨텍스트가 완전히 올라오기 전에 이벤트를 발행하려 하면 발생한다.
 * EventsInitializer가 정상적으로 동작하면 이 예외는 발생하지 않아야 한다.
 */
public class EventPublisherNotInitializedException extends RuntimeException {

    public EventPublisherNotInitializedException() {
        super("도메인 이벤트 발행기가 초기화되지 않았습니다.");
    }
}
