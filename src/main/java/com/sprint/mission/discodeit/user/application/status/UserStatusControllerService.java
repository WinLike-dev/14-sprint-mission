package com.sprint.mission.discodeit.user.application.status;

import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 온라인 상태(UserStatus) 관련 비즈니스 로직의 애플리케이션 서비스 인터페이스.
 * UserStatusController가 호출하는 경계 역할을 한다.
 */
public interface UserStatusControllerService {

    // 설계: 단건 동작은 UserStatus ID가 아니라 연결된 사용자 ID를 받는다.
    // 특정 사용자의 온라인 상태를 조회한다.
    UserStatusDto find(UUID userId);

    // 모든 사용자의 온라인 상태 목록을 조회한다.
    List<UserStatusDto> findAll();

    // 특정 사용자의 마지막 활동 시각을 현재 시각으로 갱신한다.
    UserStatusDto update(UUID userId);
}
