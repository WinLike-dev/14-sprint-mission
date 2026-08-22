package com.sprint.mission.discodeit.channel.application.readstatus;

import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;
import com.sprint.mission.discodeit.channel.domain.readstatus.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.common.exception.DuplicateAssociationException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelUserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 읽음 상태(ReadStatus) 비즈니스 로직의 실제 구현 클래스.
 * 읽음 상태의 생성, 조회, 갱신, 삭제를 처리한다.
 * PRIVATE 채널의 경우 채널 생성 시에만 ReadStatus가 자동으로 만들어지므로,
 * 이 서비스에서 직접 생성하는 것은 PUBLIC 채널에 대해서만 가능하다.
 */
@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusControllerService {

    private final ReadStatusRepository readStatusRepository; // 읽음 상태 저장소
    private final ChannelRepository channelRepository; // 채널 저장소 (채널 존재 여부 및 타입 확인용)
    private final ChannelUserReader userReader; // 사용자 존재 여부 확인용

    // 읽음 상태 생성 (PUBLIC 채널에서만 가능, PRIVATE 채널은 채널 생성 시 자동 생성됨)
    @Override
    public ReadStatusDto create(UUID userId, UUID channelId, Instant lastReadAt) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        Objects.requireNonNull(
                channelId,
                "channelId는 null일 수 없습니다."
        );
        userReader.requireExists(userId);
        Channel channel = channelRepository.getById(channelId);
        if (channel.getType() == ChannelType.PRIVATE) { // PRIVATE 채널은 별도 생성 불가
            throw new ReadStatusCreationNotAllowedException(channelId);
        }

        // 유저와 채널 id 모두 똑같은 놈 있는 지 체크 (같은 조합이 이미 있으면 중복 예외 발생)
        // 필요한 건 존재 여부뿐이라 객체를 만들지 않는 exists로 묻는다.
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new DuplicateAssociationException(
                    ReadStatus.class,
                    associationContext(userId, channelId)
            );
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

    // 마지막 읽음 시각을 현재 시각으로 갱신 (사용자가 채널을 확인했을 때 호출)
    @Override
    public ReadStatusDto updateLastReadAt(UUID readStatusId, Instant newLastReadAt) {
        ReadStatus status = readStatusRepository.getById(readStatusId);
        status.updateLastReadAt(newLastReadAt);
        return ReadStatusDto.from(readStatusRepository.update(status));
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
                        associationContext(userId, channelId)
                ));
    }

    // 예외 메시지에 포함할 연관 정보 문자열을 생성하는 헬퍼 메서드
    private String associationContext(UUID userId, UUID channelId) {
        return "userId=%s, channelId=%s".formatted(userId, channelId);
    }
}
