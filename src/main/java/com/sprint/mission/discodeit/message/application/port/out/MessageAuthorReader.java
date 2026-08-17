package com.sprint.mission.discodeit.message.application.port.out;

import java.util.UUID;

/**
 * 메시지 모듈이 작성자 존재 여부를 확인할 때 쓰는 outbound 포트.
 * 구현은 adapter.out의 MessageUserAclAdapter가 user 모듈 API를 호출한다.
 */
public interface MessageAuthorReader {

    // 주어진 authorId에 해당하는 사용자가 존재하는지 확인하고, 없으면 예외를 던진다
    void requireExists(UUID authorId);
}
