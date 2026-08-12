package com.sprint.mission.discodeit.auth.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String username) {
        super("인증에 실패했습니다. username=" + username);
    }
}
