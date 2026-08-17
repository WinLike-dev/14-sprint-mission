package com.sprint.mission.discodeit.user.application.auth.exception;

/**
 * 로그인 인증에 실패했을 때 던지는 예외.
 * username이 존재하지 않거나 password가 일치하지 않을 때 발생한다.
 * RuntimeException을 상속하므로 별도의 catch 없이도 전파된다(Unchecked 예외).
 */
public class AuthenticationFailedException extends RuntimeException {

    // 인증 실패 메시지에 시도한 username을 포함시킨다.
    public AuthenticationFailedException(String username) {
        super("인증에 실패했습니다. username=" + username);
    }
}
