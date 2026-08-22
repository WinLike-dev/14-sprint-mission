package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * 저장소 관련 설정값을 타입으로 표현한 프로퍼티.
 *
 * 설정 정책은 누락과 오류를 다르게 다룬다.
 * - 값이 없으면 파일 저장소로 fallback한다. 인메모리로 조용히 전환되어 데이터가 사라지는 편보다
 *   영속 저장소를 기본값으로 두는 편이 안전하기 때문이다.
 * - 값이 있지만 허용되지 않은 값이면 fallback하지 않고 기동 단계에서 실패한다(fail-fast).
 *   오타를 기본값으로 덮어버리면 의도하지 않은 저장소로 조용히 기동하기 때문이다.
 *
 * 잘못된 값 차단은 RepositoryTypeCondition이 조건 평가 시점에 수행한다.
 *
 * @param type     사용할 저장소 구현체 (jcf | file). 누락 시 DEFAULT_TYPE
 * @param dataRoot file 구현체가 사용할 데이터 루트 경로. 누락 시 DEFAULT_DATA_ROOT
 */
@ConfigurationProperties(prefix = RepositoryProperties.PREFIX)
public record RepositoryProperties(RepositoryType type, String dataRoot) {

    public static final String PREFIX = "discodeit.repository";
    public static final String TYPE_KEY = PREFIX + ".type";

    // 설정 누락 시 적용할 기본 저장소. 데이터가 유지되는 쪽을 기본값으로 둔다.
    public static final RepositoryType DEFAULT_TYPE = RepositoryType.FILE;

    // 설정 누락 시 적용할 기본 데이터 루트.
    public static final String DEFAULT_DATA_ROOT = "data";

    // 선택 가능한 저장소 구현체. 목록에 없는 값은 기동 단계에서 차단된다.
    public enum RepositoryType {
        JCF, FILE
    }

    // 컴팩트 생성자: 누락된 값에 기본값 정책을 적용한다.
    public RepositoryProperties {
        type = type == null ? DEFAULT_TYPE : type;
        dataRoot = (dataRoot == null || dataRoot.isBlank()) ? DEFAULT_DATA_ROOT : dataRoot;
    }

    // file 구현체가 사용할 루트 경로. type=file 일 때만 호출된다.
    public Path dataRootPath() {
        return Path.of(dataRoot);
    }
}
