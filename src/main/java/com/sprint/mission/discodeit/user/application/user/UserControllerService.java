package com.sprint.mission.discodeit.user.application.user;

import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserProfileCreateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.adapter.in.rest.user.dto.response.UserDto;

import java.util.List;
import java.util.UUID;

/**
 * 사용자(User) 관련 비즈니스 로직의 애플리케이션 서비스 인터페이스.
 * 컨트롤러(Controller)가 호출하는 경계 역할을 한다.
 * "ControllerService"라는 이름은 이 인터페이스가 컨트롤러 전용 계약임을 뜻한다.
 */
public interface UserControllerService {

    // 새 사용자를 생성한다. 프로필 이미지는 선택사항(null 가능).
    UserDto create(UserCreateRequest request, UserProfileCreateRequest profile);

    // ID로 사용자 한 명을 조회한다.
    UserDto find(UUID id);

    // 모든 사용자 목록을 조회한다.
    List<UserDto> findAll();

    // 기존 사용자 정보를 수정한다. 프로필 이미지도 함께 교체할 수 있다.
    UserDto update(UUID id, UserUpdateRequest request, UserProfileCreateRequest profile);

    // 사용자를 삭제한다. 관련 상태, 프로필 등도 함께 정리된다.
    void delete(UUID id);
}
