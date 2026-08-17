package com.sprint.mission.discodeit.user.application.port.out;

import com.sprint.mission.discodeit.common.repository.CrudRepository;
import com.sprint.mission.discodeit.user.domain.user.User;

import java.util.Optional;

/**
 * 사용자 저장소 outbound 포트.
 * username/email 조회 등 사용자 전용 쿼리를 추가로 정의한다.
 */
public interface UserRepository extends CrudRepository<User> {

    // username으로 사용자를 조회한다. 로그인 시 사용된다.
    Optional<User> findByUsername(String username);

    // 해당 username이 이미 존재하는지 확인한다. 회원가입 시 중복 검사용.
    boolean existsByUsername(String username);

    // 해당 email이 이미 존재하는지 확인한다. 회원가입 시 중복 검사용.
    boolean existsByEmail(String email);
}
