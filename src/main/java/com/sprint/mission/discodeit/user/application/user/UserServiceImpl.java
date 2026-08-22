package com.sprint.mission.discodeit.user.application.user;

import com.sprint.mission.discodeit.user.application.user.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.application.user.dto.UserResult;

import com.sprint.mission.discodeit.user.domain.user.User;
import com.sprint.mission.discodeit.user.domain.status.UserStatus;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.DuplicateFieldValueException;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserStatusRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserContentManager;
import com.sprint.mission.discodeit.user.application.port.out.UserContentData;
import com.sprint.mission.discodeit.common.event.Events;
import com.sprint.mission.discodeit.user.api.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * UserControllerService의 구현체.
 * 사용자 생성, 조회, 수정, 삭제 등 핵심 비즈니스 로직을 처리한다.
 * 프로필 이미지는 UserContentManager를 통해 다른 모듈(content)에 위임하고,
 * 온라인 상태는 UserStatusRepository로 관리한다.
 */
@Service
// final 협력 객체를 받는 생성자를 Lombok이 만들고 Spring이 Repository/Port Bean을 주입한다.
@RequiredArgsConstructor
public class UserServiceImpl implements UserControllerService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserContentManager contentManager; // 프로필 이미지 생성/삭제 outbound 포트

    // 새 사용자를 생성한다. 프로필 이미지 저장 -> User 저장 -> UserStatus 생성 순서로 진행한다.
    @Override
    public UserResult create(CreateUserCommand command) {
        CreateUserCommand target = Objects.requireNonNull(command);
        // 유일한 값인지 확인하기 유저 명과 이메일
        validateUniqueFields(null, target.username(), target.email());

        UUID createdProfileId = null;
        User user = null;

        try {
            // 프로필 이미지가 있으면 먼저 저장하고 ID를 받아온다
            createdProfileId = target.profile() == null
                    ? null
                    : contentManager.create(toContentData(target.profile()));
            // User 생성자가 id를 만들고, repository.create는 그 엔티티를 저장한 뒤 같은 인스턴스를 돌려준다.
            user = userRepository.create(new User(
                    target.username(),
                    target.email(),
                    target.password(),
                    createdProfileId
            ));
            userStatusRepository.create(new UserStatus(user.getId(), Instant.now())); // 온라인 상태 초기화
            return createResponse(user);
        } catch (RuntimeException exception) {
            // 생성 중 실패하면 이미 만든 리소스를 정리한다 (수동 롤백)
            rollbackCreatedUser(user, createdProfileId, exception);
            throw exception;
        }
    }

    // ID로 사용자 한 명을 조회하여 DTO로 변환한다.
    @Override
    public UserResult find(UUID id) {
        return createResponse(userRepository.getById(id));
    }

    // 전체 사용자를 조회하여 DTO 리스트로 반환한다.
    // 유저마다 상태를 다시 조회하면 목록 한 번이 조회 N회로 늘어나므로 O(n * n),
    // 유저검색, 유저 상태 검색 따로 각각 해주고 (O(n + n)) map으로 연결 짓기 (o(1)) 이다. O(n)
    @Override
    public List<UserResult> findAll() {
        Map<UUID, UserStatus> statusByUserId = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, Function.identity()));

        return userRepository.findAll().stream()
                .map(user -> createResponse(user, statusByUserId.get(user.getId())))
                .toList();
    }

    // 사용자 정보를 수정한다. 프로필 이미지가 새로 들어오면 교체하고 기존 것은 삭제한다.
    @Override
    public UserResult update(UUID id, UpdateUserCommand command) {
        User user = userRepository.getById(id);
        UpdateUserCommand target = Objects.requireNonNull(command);
        validateUniqueFields(id, target.username(), target.email());

        UUID previousProfileId = user.getProfileId(); // 기존 프로필 ID 보관
        UUID newProfileId = target.profile() == null
                ? null
                : contentManager.create(toContentData(target.profile())); // 새 프로필 생성
        UUID nextProfileId = newProfileId == null ? previousProfileId : newProfileId; // 새 것이 없으면 기존 유지

        try {
            // 요청에 없는 값은 기존 값을 유지한다.
            // 이 해석은 요청 형태를 아는 이 계층의 몫이고, 엔티티는 완성된 값만 받는다.
            user.update(
                    target.username() == null ? user.getUsername() : target.username(),
                    target.email() == null ? user.getEmail() : target.email(),
                    target.password() == null ? user.getPassword() : target.password(),
                    nextProfileId
            );
            userRepository.update(user);
        } catch (RuntimeException exception) {
            // 업데이트 실패 시 새로 만든 프로필만 정리한다
            if (newProfileId != null) {
                suppressCleanupFailure(
                        exception,
                        () -> contentManager.delete(newProfileId)
                );
            }
            throw exception;
        }
        // 새 프로필이 실제로 생기고 기존 프로필도 있었다면 삭제한 후 응답을 만든다.
        if (newProfileId != null && previousProfileId != null) {
            contentManager.delete(previousProfileId);
        }
        return createResponse(user);
    }

    // 사용자를 삭제한다. 상태, 프로필, 관련 이벤트까지 함께 처리한다.
    @Override
    public void delete(UUID id) {
        User user = userRepository.getById(id);
        UserStatus status = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, id));
        userStatusRepository.deleteById(status.getId()); // 상태 먼저 삭제
        userRepository.deleteById(id);                   // 사용자 삭제
        if (user.getProfileId() != null) {
            contentManager.delete(user.getProfileId());  // 프로필 이미지 삭제
                                                        // 겉다리 인데도 나중에 지운 이유 참조 당하는 놈을 나중에 지워야 참조 불가능한 것을 참조하는 불상사를 막을 수 있기 때문이다.
                                                        // 아웃 바운디드라 나중에 처리한 이유도 있음
        }
        Events.raise(new UserDeletedEvent(id)); // 다른 모듈에 사용자 삭제를 알림
    }

    // username과 email이 다른 사용자와 중복되지 않는지 검증한다.
    // currentId가 있으면 자기 자신은 제외한다(수정 시).
    // 필요한 정보는 사용자 객체가 아니라 "그 값을 쓰는 다른 사용자가 있는가" 하나뿐이다.
    // 전체 목록을 가져와 훑는 대신 존재 여부를 저장소에 묻는다.
    // File 구현에서 가입 한 번마다 전체 .ser을 역직렬화하던 비용이 사라지지는 않지만,
    // 판단이 저장소로 넘어가면서 호출부에 의도가 드러나고 구현체가 인덱스로 최적화할 자리가 생긴다.
    private void validateUniqueFields(UUID currentId, String username, String email) {
        if (username != null && userRepository.existsByUsername(username, currentId)) {
            throw new DuplicateFieldValueException(User.class, "username", username);
        }
        if (email != null && userRepository.existsByEmail(email, currentId)) {
            throw new DuplicateFieldValueException(User.class, "email", email);
        }
    }

    // 단건 조회 경로: 필요한 상태 하나만 직접 조회한다.
    private UserResult createResponse(User user) {
        return createResponse(user, userStatusRepository.findByUserId(user.getId()).orElse(null));
    }

    // User와 UserStatus.online을 유스케이스 결과로 합친다. password는 넣지 않는다.
    private UserResult createResponse(User user, UserStatus status) {
        if (status == null) {
            throw new EntityNotFoundException(UserStatus.class, user.getId());
        }
        return UserResult.from(user, status.isOnline());
    }

    // 사용자 생성 중 예외 발생 시, 이미 저장된 리소스를 수동으로 정리(롤백)한다.
    private void rollbackCreatedUser(
            User user,
            UUID profileId,
            RuntimeException original
    ) {
        if (user != null) {
            userStatusRepository.findByUserId(user.getId())
                    .ifPresent(status -> suppressCleanupFailure(
                            original,
                            () -> userStatusRepository.deleteById(status.getId())
                    ));
        }
        if (user != null && userRepository.existsById(user.getId())) {
            suppressCleanupFailure(original, () -> userRepository.deleteById(user.getId()));
        }
        if (profileId != null) {
            suppressCleanupFailure(
                    original,
                    () -> contentManager.delete(profileId)
            );
        }
    }

    // 정리 작업 중 발생한 예외를 원래 예외에 suppressed로 추가한다.
    // 정리 실패가 원래 예외를 덮어쓰지 않도록 하기 위함.
    private void suppressCleanupFailure(RuntimeException original, Runnable cleanup) {
        try {
            cleanup.run();
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }

    private UserContentData toContentData(UserProfileCommand profile) {
        return new UserContentData(
                profile.fileName(), profile.contentType(), profile.bytes()
        );
    }
}
