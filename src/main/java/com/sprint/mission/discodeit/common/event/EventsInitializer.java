package com.sprint.mission.discodeit.common.event;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Events 클래스에 Spring의 ApplicationEventPublisher를 주입해주는 초기화 담당 컴포넌트.
 * Spring Bean 생명주기를 활용하여 애플리케이션 시작 시 publisher를 설정하고,
 * 종료 시 정리(clear)한다.
 *
 * 왜 필요한가: Events 클래스는 static 유틸리티라서 Spring 의존성 주입을 직접 받을 수 없다.
 * 이 클래스가 대신 주입받아서 Events에 전달해주는 "다리" 역할을 한다.
 */
@Component
public class EventsInitializer implements InitializingBean, DisposableBean {

    private final ApplicationEventPublisher publisher; // Spring이 제공하는 이벤트 발행기

    // 생성자 주입: Spring이 ApplicationEventPublisher를 자동으로 넣어준다
    public EventsInitializer(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    // Bean 초기화 완료 후 호출: Events 클래스에 publisher를 등록한다
    public void afterPropertiesSet() {
        Events.initialize(publisher);
    }

    @Override
    // Bean 소멸 시 호출: Events 클래스에서 publisher를 제거하여 메모리 누수를 방지한다
    public void destroy() {
        Events.clear(publisher);
    }
}
