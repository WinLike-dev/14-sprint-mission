package com.sprint.mission.discodeit.channel.application.channel;

import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.domain.channel.Channel;
import com.sprint.mission.discodeit.channel.domain.channel.ChannelType;
import com.sprint.mission.discodeit.channel.domain.readstatus.ReadStatus;
import com.sprint.mission.discodeit.common.exception.DuplicateRequestValueException;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ReadStatusRepository;
import com.sprint.mission.discodeit.channel.application.port.out.ChannelUserReader;
import com.sprint.mission.discodeit.channel.api.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 채널 비즈니스 로직의 실제 구현 클래스.
 * 공개/비공개 채널의 생성, 조회, 수정, 삭제를 처리한다.
 * 비공개 채널 생성 시에는 참여자별 읽음 상태(ReadStatus)도 함께 생성한다.
 */
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelControllerService {

    private final ChannelRepository channelRepository; // 채널 저장소
    private final ReadStatusRepository readStatusRepository; // 읽음 상태 저장소
    private final ChannelUserReader userReader; // 사용자 존재 확인 outbound 포트

    // 퍼블릭 채널 생성 -> 바로 저장
    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {
        PublicChannelCreateRequest target = Objects.requireNonNull(request);
        Channel channel = Channel.publicChannel(target.name(), target.description());
        return createResponse(channelRepository.create(channel));
    }

    // 프라이빗 채널 생성 -> 1. 참여자 명단 올바른 지 체크 2. 채널 생성 3. 읽기상태 생성
    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
        List<UUID> participantIds = Objects.requireNonNull(request).participantIds();
        // set을 이용해 중복을 없게 하고, 입력 순서 유지시키기 (큰 의미는 모르겠지만 일단 디코에는 그렇게 구현되니)
        Set<UUID> uniqueParticipantIds = new LinkedHashSet<>(participantIds);
        if (uniqueParticipantIds.size() != participantIds.size()) {
            throw new DuplicateRequestValueException(Channel.class, "participantIds");
        }
        // 멤버체크 exception
        uniqueParticipantIds.forEach(userReader::requireExists);

        Channel channel = channelRepository.create(Channel.privateChannel());
        List<ReadStatus> createdStatuses = new ArrayList<>();
        try {
            for (UUID userId : uniqueParticipantIds) {
                ReadStatus status = new ReadStatus(userId, channel.getId());
                readStatusRepository.create(status);
                createdStatuses.add(status);
            }
            return createResponse(channel);
        } catch (RuntimeException exception) {
            // 실패했다면 저장소에 저장된 것들도 지워주는 원자성을 확보하기 위해 (다중 저장이므로 레포지토리 책임이라기에 애매함)
            for (ReadStatus status : createdStatuses) {
                // 삭제하는데 실패했으면 catch로 후속 오류 쌓고 다시 삭제 재개하고
                suppressCleanupFailure(exception, () -> readStatusRepository.deleteById(status.getId()));
            }
            suppressCleanupFailure(exception, () -> channelRepository.deleteById(channel.getId()));
            // 최종적으로 합쳐진 exception 보내기
            throw exception;
        }
    }

    // ID로 채널을 조회하고 DTO로 변환하여 반환
    @Override
    public ChannelDto find(UUID id) {
        return createResponse(channelRepository.getById(id));
    }

    // 사용자가 볼 수 있는 모든 채널을 조회 (PUBLIC 채널 전체 + 참여 중인 PRIVATE 채널)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        userReader.requireExists(userId);
        // set을 이용해 채널 id 중복을 없게 하기 + 등록된 ReadStatus를 통해 userid로 channelId 찾기 -> channelDTO 반환
        Set<UUID> participatedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(java.util.stream.Collectors.toSet());
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC
                        || participatedChannelIds.contains(channel.getId()))
                .map(this::createResponse)
                .toList();
    }

    // 채널 정보 수정 (요청에 없는 필드는 기존 값 유지)
    @Override
    public ChannelDto update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.getById(id);
        ChannelUpdateRequest target = Objects.requireNonNull(request);
        channel.update(
                target.name() == null ? channel.getName() : target.name(),
                target.description() == null
                        ? channel.getDescription()
                        : target.description()
        );
        return createResponse(channelRepository.update(channel));
    }

    @Override
    // 뭔가 어디는 레포지토리를 해야하고 어디는 레포지토리 이용하면 안되고 이러면 체계가 없고 불안한 느낌 일관성 떨어지는
    // 그래도 다양한 도메인과의 비즈니스 규칙이면 레포지토리가 아닌 서비스를 이용한다고 생각하면 또 문제될 건 없을지도?
    public void delete(UUID id) {
        channelRepository.getById(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
        Events.raise(new ChannelDeletedEvent(id));
    }

    // Channel 엔티티를 ChannelDto로 변환하는 헬퍼 메서드
    private ChannelDto createResponse(Channel channel) {
        // private면 참여자 ID 받고 public이면 빈값을 보낸다.
        List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList()
                : List.of();
        return ChannelDto.from(channel, participantIds);
    }

    // 정리(cleanup) 작업 중 발생한 예외를 원본 예외에 억제(suppressed) 예외로 추가하는 유틸 메서드
    // 정리 작업이 실패해도 원래 예외 정보를 잃지 않기 위해 사용한다
    private void suppressCleanupFailure(RuntimeException original, Runnable cleanup) {
        try {
            cleanup.run();
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
