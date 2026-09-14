package com.sprint.mission.discodeit.content.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 바이너리 스토리지 설정값.
 * DiscodeitApplication의 @ConfigurationPropertiesScan이 찾아 바인딩한다.
 *
 * type을 enum으로 받으면 "locla" 같은 오타가 조용히 무시되지 않고,
 * 바인딩 단계에서 어떤 키의 어떤 값이 잘못됐는지 알리며 기동이 실패한다.
 *
 * @param type  사용할 스토리지 구현 (local)
 * @param local type=local일 때 쓰는 설정
 */
@ConfigurationProperties(prefix = "discodeit.storage")
public record StorageProperties(StorageType type, Local local) {

    public static final String TYPE_KEY = "discodeit.storage.type";

    public enum StorageType {
        LOCAL
    }

    /**
     * @param rootPath 파일을 저장할 루트 디렉터리. 상대 경로는 실행 디렉터리 기준이다.
     */
    public record Local(String rootPath) {
    }
}
