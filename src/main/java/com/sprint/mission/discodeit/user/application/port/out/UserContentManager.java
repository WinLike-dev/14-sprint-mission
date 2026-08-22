package com.sprint.mission.discodeit.user.application.port.out;

import java.util.UUID;

/**
 * 사용자 프로필 이미지 생성/삭제를 위한 outbound 포트.
 * 구현은 adapter.out의 UserContentAclAdapter가 content 모듈 API를 호출한다.
 */
public interface UserContentManager {

    // 바이너리 콘텐츠(프로필 이미지)를 저장하고, 생성된 콘텐츠의 ID를 반환한다.
    UUID create(UserContentData content);

    // 해당 ID의 바이너리 콘텐츠를 삭제한다.
    void delete(UUID contentId);
}
