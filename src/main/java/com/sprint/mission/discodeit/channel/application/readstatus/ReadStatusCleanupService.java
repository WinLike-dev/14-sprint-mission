package com.sprint.mission.discodeit.channel.application.readstatus;

import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 읽음 상태 정리 유스케이스.
 * inbound 이벤트 핸들러(UserDeletedEventHandler)가 호출한다.
 */
@Service
@RequiredArgsConstructor
public class ReadStatusCleanupService {

    private final ReadStatusRepository readStatusRepository;

    // 특정 사용자의 모든 읽음 상태를 삭제 (사용자 탈퇴 시 정리 목적)
    public void deleteAllByUserId(UUID userId) {
        readStatusRepository.deleteAllByUserId(userId);
    }
}
