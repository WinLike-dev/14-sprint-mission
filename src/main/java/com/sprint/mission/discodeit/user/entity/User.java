package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.common.exception.exceptions.InvalidValueException;
import com.sprint.mission.discodeit.common.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 사용자(User) 도메인 엔티티.
 * 디스코드잇 서비스의 회원 한 명을 표현한다.
 * username, email, password 등 핵심 사용자 정보를 갖고 있으며,
 * 생성 시점부터 이메일 형식 검증 같은 불변식(항상 지켜야 하는 규칙)을 스스로 보장한다.
 */
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 조회 결과를 담을 때 사용한다
public class User extends BaseUpdatableEntity {

    // 설계: 이메일 형식은 User가 항상 지켜야 하는 불변식이므로 Entity가 검증한다.
    // 이메일 정규식 패턴 - "@" 앞뒤로 공백이 아닌 문자가 있어야 유효하다.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Column(nullable = false, unique = true, length = 50)
    private String username;   // 사용자 이름 (로그인 ID로도 사용됨)

    @Column(nullable = false, unique = true, length = 100)
    private String email;      // 이메일 주소

    @Column(nullable = false, length = 60)
    private String password;   // 비밀번호

    // 부모 User가 프로필 이미지의 생명주기를 책임진다.
    // User를 저장·삭제하면 프로필도 함께 저장·삭제되고, 교체되어 참조가 끊긴 프로필은 고아로 삭제된다.
    // FK(profile_id)를 User가 가지므로 LAZY가 실제로 동작한다. (@OneToOne 기본값은 EAGER)
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    @JoinColumn(name = "profile_id", unique = true) // 프로필 없는 사용자도 있으므로 null 허용
    private BinaryContent profile;

    // 부모 User가 상태의 생명주기를 책임진다. User를 저장·삭제하면 상태도 함께 저장·삭제된다.
    // 주인이 아닌 쪽(mappedBy)이라 FK가 users 테이블에 없어 지연 로딩이 되지 않는다.
    // 그래서 fetch 속성을 적지 않고, 목록 조회는 fetch join으로 한 번에 가져온다.
    @OneToOne(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private UserStatus status;

    // 새 사용자를 생성하는 생성자 - 필수 값 검증을 수행한다. 프로필이 없으면 null을 넘긴다.
    // "사용자는 항상 상태를 가진다"는 규칙을 엔티티가 보장하도록 상태도 여기서 함께 만든다.
    // 양방향 참조가 한곳에서 연결되므로 한쪽만 설정되는 일이 없다.
    public User(String username, String email, String password, BinaryContent profile, Instant lastActiveAt) {
        this.username = requireNonBlank(username, "username");
        this.email = requireValidEmail(email);
        this.password = requireNonBlank(password, "password");
        this.profile = profile;
        this.status = new UserStatus(this, lastActiveAt);
    }

    // 계정 정보를 수정한다. 전달된 값을 그대로 반영한다.
    // Channel.update, Message.update와 같은 계약이다. 같은 이름의 update가 엔티티마다
    // 다른 의미를 가지면 호출자가 매번 어느 규칙인지 확인해야 하므로 하나로 맞췄다.
    // "값이 없으면 기존 유지"라는 부분 수정 해석은 요청을 아는 service 계층이 담당한다.
    // 프로필 교체는 파일 저장이 얽혀 있어 updateProfile로 따로 둔다.
    public void update(String username, String email, String password) {
        // 검증을 모두 통과한 뒤에 대입한다.
        // 대입과 검증을 섞으면 중간에 예외가 났을 때 일부 필드만 바뀐 상태가 남는다.
        String nextUsername = requireNonBlank(username, "username");
        String nextEmail = requireValidEmail(email);
        String nextPassword = requireNonBlank(password, "password");

        this.username = nextUsername;
        this.email = nextEmail;
        this.password = nextPassword;
    }

    // 프로필 이미지를 교체한다. 이전 프로필은 참조가 끊겨 orphanRemoval로 flush 때 행이 삭제된다.
    public void updateProfile(BinaryContent newProfile) {
        this.profile = Objects.requireNonNull(newProfile, "newProfile은 null일 수 없습니다.");
    }

    // 값이 null이거나 빈 문자열이면 예외를 던진다. 필수 입력 값 검증용.
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }

    // 이메일이 올바른 형식인지 정규식으로 검증한다.
    private static String requireValidEmail(String email) {
        String value = requireNonBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidValueException("email 형식이 올바르지 않습니다.");
        }
        return value;
    }
}
