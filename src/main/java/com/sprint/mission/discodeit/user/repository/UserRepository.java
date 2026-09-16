package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 저장소.
 * username/email 조회 등 사용자 전용 쿼리를 메서드 이름 기반 쿼리로 정의한다.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    // 사용자 목록은 online 때문에 상태가 항상 필요하므로 한 번의 조인으로 함께 가져온다.
    // User.status는 지연 로딩이 되지 않아, 이게 없으면 사용자마다 상태 조회가 한 번씩 더 나간다(N+1).
    @Override
    @EntityGraph(attributePaths = {"status", "profile"})
    List<User> findAll();

    // 채널 참여자처럼 여러 사용자를 한 번에 응답에 담을 때 쓴다.
    // 상태와 프로필을 함께 가져오지 않으면 사용자 수만큼 조회가 더 나간다.
    @EntityGraph(attributePaths = {"status", "profile"})
    List<User> findAllByIdIn(Collection<UUID> ids);

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
