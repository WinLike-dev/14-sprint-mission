package com.sprint.mission.discodeit.user.service.dto;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;
import com.sprint.mission.discodeit.user.entity.User;

import java.util.UUID;

/**
 * 사용자 유스케이스가 밖으로 내보내는 결과.
 * User 엔티티를 그대로 노출하지 않는다. password가 있고,
 * 응답에 필요한 online은 User가 아니라 UserStatus에서 오기 때문이다.
 * 프로필은 id만이 아니라 메타 정보까지 담는다. 클라이언트가 파일명과 유형을 바로 쓰기 때문이다.
 * REST UserDto는 이 결과를 HTTP 필드로만 옮긴다.
 */
public record UserResult(
        UUID id,
        String username,
        String email,
        BinaryContentResult profile,
        boolean online
) {

    // 온라인 여부는 상태가 스스로 판단한다. User는 생성될 때 상태를 함께 만들므로 항상 존재한다.
    public static UserResult from(User user) {
        return new UserResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                BinaryContentResult.from(user.getProfile()),
                user.getStatus().isOnline()
        );
    }
}
