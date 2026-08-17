package com.sprint.mission.discodeit.user.domain.user;

import com.sprint.mission.discodeit.common.entity.BaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 사용자(User) 도메인 엔티티.
 * 디스코드잇 서비스의 회원 한 명을 표현한다.
 * username, email, password 등 핵심 사용자 정보를 갖고 있으며,
 * 생성 시점부터 이메일 형식 검증 같은 불변식(항상 지켜야 하는 규칙)을 스스로 보장한다.
 */
@Getter
public class User extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    // 설계: 이메일 형식은 User가 항상 지켜야 하는 불변식이므로 Entity가 검증한다.
    // 이메일 정규식 패턴 - "@" 앞뒤로 공백이 아닌 문자가 있어야 유효하다.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private String username;   // 사용자 이름 (로그인 ID로도 사용됨)
    private String email;      // 이메일 주소
    private String password;   // 비밀번호
    private UUID profileId;    // 프로필 이미지(BinaryContent)의 ID, 없으면 null

    // 새 사용자를 생성하는 생성자 - 필수 값 검증을 수행한다.
    public User(String username, String email, String password, UUID profileId) {
        this.username = requireNonBlank(username, "username");
        this.email = requireValidEmail(email);
        this.password = requireNonBlank(password, "password");
        this.profileId = profileId;
    }

    // DB나 파일에서 복원할 때 사용하는 내부 생성자 - id, 생성/수정 시각까지 모두 받는다.
    private User(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            String password,
            UUID profileId
    ) {
        super(id, createdAt, updatedAt);
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    // 사용자 정보를 수정한다. 전달된 값을 그대로 반영한다.
    // Channel.update, Message.update와 같은 계약이다. 같은 이름의 update가 엔티티마다
    // 다른 의미를 가지면 호출자가 매번 어느 규칙인지 확인해야 하므로 하나로 맞췄다.
    // "값이 없으면 기존 유지"라는 부분 수정 해석은 요청을 아는 application 계층이 담당한다.
    public void update(String username, String email, String password, UUID profileId) {
        // 검증을 모두 통과한 뒤에 대입한다.
        // 대입과 검증을 섞으면 중간에 예외가 났을 때 일부 필드만 바뀐 상태가 남는다.
        String nextUsername = requireNonBlank(username, "username");
        String nextEmail = requireValidEmail(email);
        String nextPassword = requireNonBlank(password, "password");

        this.username = nextUsername;
        this.email = nextEmail;
        this.password = nextPassword;
        this.profileId = profileId;
        markUpdated(); // 수정 시각을 현재 시각으로 갱신
    }

    // 깊은 복사본을 만든다. 원본 객체 변경 없이 안전하게 다룰 때 사용한다.
    public User copy() {
        return new User(
                getId(),
                getCreatedAt(),
                getUpdatedAt(),
                username,
                email,
                password,
                profileId
        );
    }

    // 값이 null이거나 빈 문자열이면 예외를 던진다. 필수 입력 값 검증용.
    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "은(는) 비어 있을 수 없습니다.");
        }
        return value;
    }

    // 이메일이 올바른 형식인지 정규식으로 검증한다.
    private static String requireValidEmail(String email) {
        String value = requireNonBlank(email, "email");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("email 형식이 올바르지 않습니다.");
        }
        return value;
    }
}
