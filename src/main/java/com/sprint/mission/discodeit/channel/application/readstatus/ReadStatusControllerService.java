package com.sprint.mission.discodeit.channel.application.readstatus;

import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 읽음 상태(ReadStatus) 관련 비즈니스 로직의 인터페이스.
 * 컨트롤러(ReadStatusController)가 이 인터페이스에 의존하여 구현체와 분리된다.
 *
 * 대상은 모두 (userId, channelId)로 지정한다.
 * 한 사용자는 한 채널에 대해 읽음 상태를 하나만 가지므로 그 조합이 이 엔티티의 식별자다.
 * id는 저장을 위한 번호일 뿐이어서 호출자가 알 필요가 없다.
 */
public interface ReadStatusControllerService {

    // 읽음 시각을 주어진 값으로 만든다. 없으면 만들고 있으면 갱신한다.
    // 호출자의 의도는 "이 채널을 여기까지 읽었다" 하나뿐이라 생성과 갱신을 나누지 않는다.
    ReadStatusDto upsert(UUID userId, UUID channelId, Instant lastReadAt);

    // 특정 사용자 + 특정 채널의 읽음 상태 단건 조회
    ReadStatusDto find(UUID userId, UUID channelId);

    // 특정 사용자의 모든 읽음 상태 조회
    List<ReadStatusDto> findAllByUserId(UUID userId);

    // 읽음 상태 삭제
    void delete(UUID userId, UUID channelId);
}
