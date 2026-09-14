package com.sprint.mission.discodeit.content.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

/**
 * discodeit.storage.type 값에 맞는 BinaryContentStorage 구현체를 Bean으로 등록한다.
 */
@Configuration
public class BinaryContentStorageConfig {

    // discodeit.storage.type=local 일 때만 등록한다.
    // @Bean으로 만든 객체에도 @PostConstruct가 호출되므로 init()이 자동으로 실행된다.
    @Bean
    @ConditionalOnProperty(name = StorageProperties.TYPE_KEY, havingValue = "local")
    public BinaryContentStorage localBinaryContentStorage(StorageProperties properties) {
        StorageProperties.Local local = properties.local();
        if (local == null || local.rootPath() == null || local.rootPath().isBlank()) {
            throw new IllegalStateException(
                    "discodeit.storage.local.root-path 설정이 필요합니다."
            );
        }
        return new LocalBinaryContentStorage(Path.of(local.rootPath()));
    }
}
