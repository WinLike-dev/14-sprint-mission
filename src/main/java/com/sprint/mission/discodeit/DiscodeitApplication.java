package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Discodeit 애플리케이션의 시작점(Entry Point).
 * Spring Boot가 이 클래스를 기준으로 하위 패키지의 모든 Bean(@Component, @Service, @Repository 등)을
 * 자동으로 스캔하고 등록한다.
 *
 * @SpringBootApplication: 자동 설정, 컴포넌트 스캔, 추가 설정 등록을 한꺼번에 해주는 어노테이션이다.
 * @ConfigurationPropertiesScan: RepositoryProperties처럼 설정값을 타입으로 표현한 클래스를 찾아 바인딩한다.
 */
// Spring Boot가 현재 패키지 아래의 Configuration과 Service Bean을 탐색하는 진입점이다.
@SpringBootApplication
@ConfigurationPropertiesScan
public class DiscodeitApplication {

    // 애플리케이션을 실행하는 메인 메서드. JVM이 가장 먼저 호출한다.
    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args); // Spring Boot 애플리케이션을 구동한다
    }
}
