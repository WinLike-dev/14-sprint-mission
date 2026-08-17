package com.sprint.mission.discodeit.common.event;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 모듈이 공개 이벤트를 올릴 때 쓰는 공통 버스 헬퍼.
 * application 유스케이스가 Events.raise를 호출하고,
 * Spring ApplicationEventPublisher로 전달한다.
 * 비즈니스 outbound 포트가 아니라 기술 장치라 common에 둔다.
 */
public final class Events {

    // AtomicReference를 사용하는 이유: 멀티스레드 환경에서도 안전하게 publisher를 교체하기 위함이다.
    private static final AtomicReference<ApplicationEventPublisher> PUBLISHER =
            new AtomicReference<>();

    // 인스턴스 생성 방지: 유틸리티 클래스이므로 new Events()로 생성할 수 없다
    private Events() {
    }

    // 도메인 이벤트를 발행한다. 초기화되지 않은 상태에서 호출하면 예외가 발생한다.
    public static void raise(Object event) {
        ApplicationEventPublisher publisher = PUBLISHER.get();
        if (publisher == null) {
            throw new EventPublisherNotInitializedException();
        }
        publisher.publishEvent(Objects.requireNonNull(event, "event는 null일 수 없습니다."));
    }

    // EventsInitializer에 의해 애플리케이션 시작 시 호출된다. publisher를 설정한다.
    static void initialize(ApplicationEventPublisher publisher) {
        PUBLISHER.set(Objects.requireNonNull(publisher));
    }

    // 애플리케이션 종료 시 호출된다. compareAndSet으로 현재 등록된 publisher만 제거한다.
    static void clear(ApplicationEventPublisher publisher) {
        PUBLISHER.compareAndSet(publisher, null);
    }
}
