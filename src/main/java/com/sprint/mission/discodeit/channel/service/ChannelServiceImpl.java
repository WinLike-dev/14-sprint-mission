package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.common.exception.DuplicateRequestValueException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.message.service.InternalMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelControllerService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    // internal vs controller 인데 각각 내부 서비스 동작과 외부 요청을 의미한다. 여기선 내부 동작 메세지 서비스
    // 도메인 서비스는 레포지토리를 완전히 감싸야하는가?
    private final InternalMessageService internalMessageService;

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
        uniqueParticipantIds.forEach(userRepository::getById);

        Channel channel = channelRepository.create(Channel.privateChannel());
        List<ReadStatus> createdStatuses = new ArrayList<>();
        try {
            Instant now = Instant.now();
            for (UUID userId : uniqueParticipantIds) {
                ReadStatus status = new ReadStatus(userId, channel.getId(), now);
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

    @Override
    public ChannelDto find(UUID id) {
        return createResponse(channelRepository.getById(id));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        userRepository.getById(userId);
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
        // 메세지만 서비스에 맡기는 이유는 cascade를 위해서
        internalMessageService.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
    }

    private ChannelDto createResponse(Channel channel) {
        // private면 참여자 ID 받고 public이면 빈값을 보낸다.
        List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList()
                : List.of();
        // 마지막에 읽은 메세지 확인
        Instant lastMessageAt = messageRepository.findLatestByChannelId(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);
        return ChannelDto.from(channel, lastMessageAt, participantIds);
    }

    private void suppressCleanupFailure(RuntimeException original, Runnable cleanup) {
        try {
            cleanup.run();
        } catch (RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
