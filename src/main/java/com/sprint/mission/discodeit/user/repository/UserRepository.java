package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 저장소.
 * username/email 조회 등 사용자 전용 쿼리를 메서드 이름 기반 쿼리로 정의한다.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // username으로 사용자를 조회한다. 로그인 시 사용된다.
    Optional<User> findByUsername(String username);

    // 해당 username을 쓰는 사용자가 있는지 확인한다. (가입 시)
    boolean existsByUsername(String username);

    // 해당 email을 쓰는 사용자가 있는지 확인한다. (가입 시)
    boolean existsByEmail(String email);

    // id가 다른 사용자 중에 같은 username을 쓰는 사용자가 있는지 확인한다.
    // 수정 시 자기 자신은 중복 대상에서 빼야 하기 때문이다.
    boolean existsByUsernameAndIdNot(String username, UUID id);

    // id가 다른 사용자 중에 같은 email을 쓰는 사용자가 있는지 확인한다. (수정 시)
    boolean existsByEmailAndIdNot(String email, UUID id);
}
