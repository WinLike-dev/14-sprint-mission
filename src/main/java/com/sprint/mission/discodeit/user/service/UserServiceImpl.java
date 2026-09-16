package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.service.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.service.dto.UserResult;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.exceptions.DuplicateFieldValueException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.content.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager;
import com.sprint.mission.discodeit.channel.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 프로필 이미지 행은 User의 cascade로 함께 저장·삭제되고, 파일은 BinaryContentFileManager가 트랜잭션에 맞춰 다룬다.
 * 온라인 상태는 UserStatusRepository로 관리한다.
 */
@Service
// final 협력 객체를 받는 생성자를 Lombok이 만들고 Spring이 Bean을 주입한다.
@RequiredArgsConstructor
public class UserServiceImpl implements UserControllerService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository; // 프로필 교체 시 새 프로필 저장용
    private final BinaryContentFileManager fileManager;            // 프로필 파일 저장·삭제
    private final ReadStatusRepository readStatusRepository;       // 사용자 삭제 시 읽음 상태 정리용

    // 새 사용자를 생성한다. User 저장(프로필 포함) -> 프로필 파일 저장 -> UserStatus 생성 순서로 진행한다.
    // 중간에 실패하면 트랜잭션이 롤백되어 저장한 행이 사라지고, 저장한 파일은 fileManager가 지운다.
    @Override
    @Transactional
    public UserResult create(CreateUserCommand command) {
        CreateUserCommand target = Objects.requireNonNull(command);
        // 유일한 값인지 확인하기 유저 명과 이메일
        validateUniqueFields(null, target.username(), target.email());

        BinaryContent profile = target.profile() == null ? null : toBinaryContent(target.profile());
        // 새 엔티티는 id가 없으므로 save가 persist하고, persist가 cascade되어 profile도 이 시점에 id를 받는다.
        User user = userRepository.save(new User(
                target.username(),
                target.email(),
                target.password(),
                profile
        ));
        if (profile != null) {
            fileManager.save(profile.getId(), target.profile().bytes());
        }
        userStatusRepository.save(new UserStatus(user.getId(), Instant.now())); // 온라인 상태 초기화
        return createResponse(user);
    }

    // ID로 사용자 한 명을 조회하여 DTO로 변환한다.
    @Override
    public UserResult find(UUID id) {
        return createResponse(getUser(id));
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
    @Transactional
    public UserResult update(UUID id, UpdateUserCommand command) {
        User user = getUser(id);
        UpdateUserCommand target = Objects.requireNonNull(command);
        validateUniqueFields(id, target.username(), target.email());

        // 요청에 없는 값은 기존 값을 유지한다.
        // 이 해석은 요청 형태를 아는 이 계층의 몫이고, 엔티티는 완성된 값만 받는다.
        user.update(
                target.username() == null ? user.getUsername() : target.username(),
                target.email() == null ? user.getEmail() : target.email(),
                target.password() == null ? user.getPassword() : target.password()
        );
        if (target.profile() != null) {
            replaceProfile(user, target.profile());
        }
        // 영속 상태라 변경 감지로 반영되므로 save를 부르지 않는다.
        return createResponse(user);
    }

    // 사용자를 삭제한다. 상태, 읽음 상태, 프로필까지 함께 처리한다.
    @Override
    @Transactional
    public void delete(UUID id) {
        User user = getUser(id);
        UserStatus status = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException(UserStatus.class, id));
        UUID profileId = user.getProfile() == null ? null : user.getProfile().getId();

        userStatusRepository.delete(status);             // 상태 삭제
        readStatusRepository.deleteAllByUserId(id);      // 사용자의 읽음 상태 삭제
        userRepository.delete(user);                     // 사용자 삭제. cascade REMOVE로 프로필 행도 함께 삭제된다
        if (profileId != null) {
            fileManager.deleteAfterCommit(profileId);    // 프로필 파일은 커밋이 확정된 뒤 삭제한다
        }
    }

    // username과 email이 다른 사용자와 중복되지 않는지 검증한다.
    // currentId가 있으면 자기 자신은 제외한다(수정 시).
    // 필요한 정보는 사용자 객체가 아니라 "그 값을 쓰는 다른 사용자가 있는가" 하나뿐이다.
    // 전체 목록을 가져와 훑는 대신 존재 여부를 저장소에 묻는다.
    private void validateUniqueFields(UUID currentId, String username, String email) {
        if (username != null && isUsernameTaken(username, currentId)) {
            throw new DuplicateFieldValueException(User.class, "username", username);
        }
        if (email != null && isEmailTaken(email, currentId)) {
            throw new DuplicateFieldValueException(User.class, "email", email);
        }
    }

    // 가입(currentId == null)이면 전체에서, 수정이면 자기 자신을 뺀 나머지에서 찾는다.
    private boolean isUsernameTaken(String username, UUID currentId) {
        return currentId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, currentId);
    }

    private boolean isEmailTaken(String email, UUID currentId) {
        return currentId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, currentId);
    }

    // ID로 사용자를 조회하고, 없으면 예외를 던지는 헬퍼 메서드
    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
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

    // 프로필 이미지를 교체한다.
    // 이미 영속 상태인 User에 새 객체를 넣기만 하면 cascade PERSIST가 flush 때 실행되어 그 전까지 id가 없다.
    // 파일 저장에 id가 필요하므로 새 프로필은 먼저 명시적으로 저장한다.
    private void replaceProfile(User user, UserProfileCommand profileCommand) {
        BinaryContent oldProfile = user.getProfile();
        BinaryContent newProfile = binaryContentRepository.save(toBinaryContent(profileCommand));
        fileManager.save(newProfile.getId(), profileCommand.bytes());

        user.updateProfile(newProfile); // 이전 프로필 행은 orphanRemoval로 삭제된다
        if (oldProfile != null) {
            fileManager.deleteAfterCommit(oldProfile.getId());
        }
    }

    // 프로필 입력을 메타 정보만 가진 BinaryContent로 만든다. 바이트는 fileManager가 따로 저장한다.
    private BinaryContent toBinaryContent(UserProfileCommand profile) {
        return new BinaryContent(profile.fileName(), profile.bytes().length, profile.contentType());
    }
}
