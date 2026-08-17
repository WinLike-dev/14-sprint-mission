package com.sprint.mission.discodeit.message.adapter.out.integration.user;

import com.sprint.mission.discodeit.message.application.port.out.MessageAuthorReader;
import com.sprint.mission.discodeit.user.api.UserInternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * MessageAuthorReader outbound 포트의 어댑터.
 * user 모듈의 노출 API를 호출하고, 메시지 모듈이 user 구현에 직접 의존하지 않게 막는다.
 */
@Component
@RequiredArgsConstructor
public class MessageUserAcl implements MessageAuthorReader {

    private final UserInternalApi userInternalApi; // User 모듈이 제공하는 내부 API

    // User 모듈의 내부 API를 호출하여 작성자 존재 여부를 확인한다
    @Override
    public void requireExists(UUID authorId) {
        userInternalApi.requireExists(authorId);
    }
}
