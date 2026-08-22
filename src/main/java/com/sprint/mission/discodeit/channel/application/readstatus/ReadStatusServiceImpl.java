package com.sprint.mission.discodeit.channel.application.readstatus;

import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;
import com.sprint.mission.discodeit.channel.domain.readstatus.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelUserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽음 상태(ReadStatus) 비즈니스 로직의 실제 구현 클래스.
 * 대상은 언제나 (userId, channelId)로 지정한다.
 * PRIVATE 채널의 읽음 상태는 채널 생성 시 참여자별로 함께 만들어지므로,
 * 이 서비스가 새로 만드는 것은 PUBLIC 채널에 대해서만 가능하다.
 */
@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusControllerService {

    private final ReadStatusRepository readStatusRepository; // 읽음 상태 저장소
    private final ChannelRepository channelRepository; // 채널 저장소 (채널 존재 여부 및 타입 확인용)
    private final ChannelUserReader userReader; // 사용자 존재 여부 확인용

    // 읽음 시각을 주어진 값으로 만든다. 없으면 만들고 있으면 갱신한다.
    //
    // 생성과 갱신을 나누면 호출자가 "이미 있는지"를 먼저 알아야 올바른 연산을 고를 수 있고,
    // 잘못 고르면 중복 충돌로 거절된다. 조합이 유일하다는 사실은 서버가 이미 아는 것이므로
    // 여기서 흡수한다. 같은 요청을 몇 번 보내도 결과가 같다.
    @Override
    public ReadStatusDto upsert(UUID userId, UUID channelId, Instant lastReadAt) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");
        Objects.requireNonNull(lastReadAt, "lastReadAt은 null일 수 없습니다.");

        Optional<ReadStatus> existing =
                readStatusRepository.findByUserIdAndChannelId(userId, channelId);
        if (existing.isPresent()) {
            // 이미 있다는 것은 만들 때 사용자와 채널을 확인했다는 뜻이다.
            // 둘 중 하나가 사라지면 읽음 상태도 함께 지워지므로 여기서 다시 묻지 않는다.
            ReadStatus status = existing.get();
            status.updateLastReadAt(lastReadAt);
            return ReadStatusDto.from(readStatusRepository.update(status));
        }

        // 새로 만드는 경우에만 참조 대상과 채널 종류를 확인한다.
        userReader.requireExists(userId);
        Channel channel = channelRepository.getById(channelId);
        if (channel.getType() == ChannelType.PRIVATE) { // PRIVATE 채널은 별도 생성 불가
            throw new ReadStatusCreationNotAllowedException(channelId);
        }
        ReadStatus status = new ReadStatus(userId, channelId, lastReadAt);
        return ReadStatusDto.from(readStatusRepository.create(status));
    }

    // 특정 사용자 + 특정 채널의 읽음 상태를 조회
    @Override
    public ReadStatusDto find(UUID userId, UUID channelId) {
        return ReadStatusDto.from(getByUserIdAndChannelId(userId, channelId));
    }

    // 특정 사용자의 모든 채널 읽음 상태를 조회
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        userReader.requireExists(userId);
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto::from)
                .toList();
    }

    // 읽음 상태 삭제 (사용자 + 채널 조합으로 찾아서 삭제)
    @Override
    public void delete(UUID userId, UUID channelId) {
        ReadStatus status = getByUserIdAndChannelId(userId, channelId);
        readStatusRepository.deleteById(status.getId());
    }

    // userId + channelId 조합으로 ReadStatus를 조회하고, 없으면 예외를 던지는 헬퍼 메서드
    private ReadStatus getByUserIdAndChannelId(UUID userId, UUID channelId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        Objects.requireNonNull(
                channelId,
                "channelId는 null일 수 없습니다."
        );
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ReadStatus.class,
                        "userId=%s, channelId=%s".formatted(userId, channelId)
                ));
    }
}
