package com.sprint.mission.discodeit.user.application.auth;

import com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.user.application.user.dto.UserResult;
import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.user.application.auth.exception.AuthenticationFailedException;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

/**
 * AuthControllerService의 구현체.
 * 로그인 시 username/password를 확인하고,
 * 로그인 성공 시 마지막 활동 시각을 갱신하여 온라인 상태를 유지한다.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthControllerService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    // 로그인 처리: username과 password가 일치하는 사용자를 찾고, 활동 시각을 갱신한 뒤 DTO를 반환한다.
    @Override
    public UserResult login(LoginRequest request) { // valid 를 넣어서
        LoginRequest target = Objects.requireNonNull(request);
        // username와 password 모두 일치하는 유저 찾기 -> 없으면 인증 실패 예외 발생
        User user = userRepository.findByUsername(target.username())
                .filter(found -> found.getPassword().equals(target.password()))
                .orElseThrow(() -> new AuthenticationFailedException(target.username()));

        // userStatus 찾기 없으면 EntityNotFound 예외 발생
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, user.getId()));

        // 가장 최근 ActiveAt이 된 기간 업데이트
        status.updateLastActiveAt(Instant.now()); // 로그인했으므로 "지금 활동 중"으로 갱신
        userStatusRepository.update(status);

        return UserResult.from(user, status.isOnline());
    }
}
