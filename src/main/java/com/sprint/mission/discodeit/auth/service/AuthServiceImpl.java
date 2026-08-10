package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.auth.exception.AuthenticationFailedException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthControllerService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto login(LoginRequest request) { // valid 를 넣어서
        LoginRequest target = Objects.requireNonNull(request);
        // username와 password 모두 일치하는 유저 찾기 -> 없으면 인증 실패 예외 발생
        User user = userRepository.findByUsername(target.username())
                .filter(found -> found.getPassword().equals(target.password()))
                .orElseThrow(() -> new AuthenticationFailedException(target.username()));

        // userStatus 찾기 없으면 EntityNotFound 예외 발생
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, user.getId()));

        // 가장 최근 ActiveAt이 된 기간 업데이트
        status.updateLastActiveAt();
        userStatusRepository.update(status);

        // user 폴더의 Dto 생성 (user 내용 + 현재 온라인 인지 계산 결과)
        return UserDto.from(user, status.isOnline());
    }
}
