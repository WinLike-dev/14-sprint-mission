package com.sprint.mission.discodeit.user.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.user.domain.user.User;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 저장소 outbound 포트.
 * username/email 조회 등 사용자 전용 쿼리를 추가로 정의한다.
 *
 * 파생 조회는 저장 기술과 무관한 도메인 기준 필터링이므로 default로 한 번만 정의한다.
 * 구현체가 인덱스 등으로 더 잘할 수 있다면 개별적으로 재정의하면 된다.
 */
public interface UserRepository extends CrudRepository<User> {

    // username으로 사용자를 조회한다. 로그인 시 사용된다.
    default Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    // 해당 username을 쓰는 사용자가 있는지 확인한다.
    default boolean existsByUsername(String username) {
        return existsByUsername(username, null);
    }

    // 해당 email을 쓰는 사용자가 있는지 확인한다.
    default boolean existsByEmail(String email) {
        return existsByEmail(email, null);
    }

    // excludedId를 제외하고 같은 username을 쓰는 사용자가 있는지 확인한다.
    // 수정 시 자기 자신은 중복 대상에서 빼야 하므로, 그 판단까지 저장소가 맡는다.
    // 호출부에서 전체 목록을 훑지 않아도 되고, 구현체는 인덱스로 최적화할 여지가 생긴다.
    default boolean existsByUsername(String username, UUID excludedId) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .anyMatch(user -> !user.getId().equals(excludedId));
    }

    // excludedId를 제외하고 같은 email을 쓰는 사용자가 있는지 확인한다.
    default boolean existsByEmail(String email, UUID excludedId) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .anyMatch(user -> !user.getId().equals(excludedId));
    }
}
