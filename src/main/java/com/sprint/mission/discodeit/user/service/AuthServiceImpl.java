package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.service.dto.LoginCommand;
import com.sprint.mission.discodeit.user.service.dto.UserResult;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.exception.AuthenticationFailedException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

/**
 * AuthControllerService의 구현체.
 * 로그인 시 username/password를 확인하고,
 * 로그인 성공 시 마지막 활동 시각을 갱신하여 온라인 상태를 유지한다.
 */
@Service
@RequiredArgsConstructor
// 기본은 읽기 전용 트랜잭션이다. 쓰기 메서드만 @Transactional로 덮어쓴다.
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthControllerService {

    private final UserRepository userRepository;

    // 로그인 처리: username과 password가 일치하는 사용자를 찾고, 활동 시각을 갱신한 뒤 DTO를 반환한다.
    @Override
    @Transactional
    public UserResult login(LoginCommand command) {
        LoginCommand target = Objects.requireNonNull(command);
        // username와 password 모두 일치하는 유저 찾기 -> 없으면 인증 실패 예외 발생
        User user = userRepository.findByUsername(target.username())
                .filter(found -> found.getPassword().equals(target.password()))
                .orElseThrow(() -> new AuthenticationFailedException(target.username()));

        // 가장 최근 ActiveAt이 된 기간 업데이트
        // 영속 상태라 변경 감지로 반영되므로 save를 부르지 않는다.
        UserStatus status = user.getStatus();
        status.updateLastActiveAt(Instant.now()); // 로그인했으므로 "지금 활동 중"으로 갱신

        return UserResult.from(user, status.isOnline());
    }
}
