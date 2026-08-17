package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.RepositoryProperties.RepositoryType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 저장소 구현체 설정값이 허용된 값인지 조건 평가 시점에 확인한다.
 *
 * @ConditionalOnProperty만 사용하면 "jfc" 같은 오타는 어느 조건에도 맞지 않아
 * 두 Config가 모두 조용히 스킵된다. 그 결과 기동은 실패하지만 화면에 남는 것은
 * NoSuchBeanDefinitionException뿐이고, 원인이 설정 오타라는 사실은 드러나지 않는다.
 * 개발자가 Bean 생성 실패에서 설정값 문제를 역으로 추적해야 하는 구조가 된다.
 *
 * 조건 평가는 어떤 Bean이 만들어지기도 전에 끝나므로, 여기서 값을 확인하면
 * 잘못된 설정이 원인 그대로 드러난 채 기동이 중단된다.
 * OnPropertyCondition보다 먼저 실행되어야 하므로 최우선 순위를 가진다.
 * (조건은 정렬 후 하나라도 false를 반환하면 나머지를 평가하지 않는다.)
 *
 * 값이 없는 경우는 오류가 아니라 fallback 대상이므로 통과시킨다. RepositoryProperties 참고.
 */
public class RepositoryTypeCondition implements Condition, Ordered {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String value = context.getEnvironment().getProperty(RepositoryProperties.TYPE_KEY);

        // 설정 자체가 없는 경우만 fallback 대상이다.
        // 값이 비어 있는 경우는 설정하려다 만 상태이므로 누락과 구분해 오류로 다룬다.
        // @ConditionalOnProperty는 빈 문자열도 "존재하는 값"으로 보기 때문에
        // matchIfMissing이 적용되지 않아, 여기서 잡지 않으면 다시 조용히 스킵된다.
        if (value == null) {
            return true;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        boolean allowed = Arrays.stream(RepositoryType.values())
                .anyMatch(type -> type.name().equals(normalized));

        if (!allowed) {
            throw new IllegalStateException(
                    "%s 설정값이 올바르지 않습니다. value=%s, 허용값: %s"
                            .formatted(RepositoryProperties.TYPE_KEY, value, allowedValues())
            );
        }

        // 값이 유효하면 실제 구현체 선택은 각 Config의 @ConditionalOnProperty가 담당한다.
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private static String allowedValues() {
        return Arrays.stream(RepositoryType.values())
                .map(type -> type.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" | "));
    }
}
