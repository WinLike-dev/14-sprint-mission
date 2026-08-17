package com.sprint.mission.discodeit.message.application.port.out;

import java.util.UUID;

/**
 * 메시지 첨부파일 생성/삭제를 위한 outbound 포트.
 * 구현은 adapter.out의 MessageContentAclAdapter가 content 모듈 API를 호출한다.
 */
public interface MessageContentManager {

    // 첨부파일을 생성하고, 생성된 BinaryContent의 ID를 반환한다
    UUID create(MessageContentData content);

    // 주어진 ID의 첨부파일을 삭제한다
    void delete(UUID contentId);
}
