package com.sprint.mission.discodeit.common.config;

import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * 트랜잭션 매니저 설정.
 * 서비스는 클래스 단위로 읽기 전용 트랜잭션을 기본값으로 두고 쓰기 메서드만 덮어쓰는데,
 * 덮어쓰기를 빠뜨리면 읽기 전용 트랜잭션 안에서 저장이 일어난다.
 * 이때 Hibernate의 flush 모드가 MANUAL이라 INSERT/UPDATE가 조용히 사라질 수 있어,
 * 그런 참여를 예외로 바꿔 기동 후 첫 호출에서 드러나게 한다.
 */
@Configuration
public class TransactionConfig {

    // 읽기 전용 트랜잭션에 쓰기 트랜잭션이 참여하면 IllegalTransactionStateException을 던진다.
    @Bean
    public TransactionManagerCustomizer<AbstractPlatformTransactionManager> validateReadOnlyParticipation() {
        return transactionManager -> transactionManager.setValidateExistingTransaction(true);
    }
}
