package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.service.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.UpdatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.entity.ReadStatus;
import com.sprint.mission.discodeit.common.exception.exceptions.DuplicateRequestValueException;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.content.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository.ChannelLastMessageAt;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository; // 사용자 존재 확인용
    private final MessageRepository messageRepository; // 마지막 메시지 시각 조회, 채널 삭제 시 메시지 정리용
    private final BinaryContentRepository binaryContentRepository; // 채널 삭제 시 첨부파일 정리용
    private final BinaryContentFileManager fileManager; // 채널 삭제 시 첨부파일의 실제 파일 정리용

    // 퍼블릭 채널 생성 -> 바로 저장
    @Override
    public ChannelResult createPublic(CreatePublicChannelCommand command) {
        CreatePublicChannelCommand target = Objects.requireNonNull(command);
        Channel channel = Channel.publicChannel(target.name(), target.description());
        return createChannelResult(channelRepository.save(channel));
    }

    // 프라이빗 채널 생성 -> 1. 참여자 명단 올바른 지 체크 2. 채널 생성 3. 읽기상태 생성
    @Override
    public ChannelResult createPrivate(CreatePrivateChannelCommand command) {
        List<UUID> participantIds = Objects.requireNonNull(command).participantIds();
        // set을 이용해 중복을 없게 하고, 입력 순서 유지시키기 (큰 의미는 모르겠지만 일단 디코에는 그렇게 구현되니)
        Set<UUID> uniqueParticipantIds = new LinkedHashSet<>(participantIds);
        if (uniqueParticipantIds.size() != participantIds.size()) {
            throw new DuplicateRequestValueException(Channel.class, "participantIds");
        }
        // 멤버체크 exception
        uniqueParticipantIds.forEach(this::requireUserExists);

        Channel channel = channelRepository.save(Channel.privateChannel());
        List<ReadStatus> createdStatuses = new ArrayList<>();
        try {
            for (UUID userId : uniqueParticipantIds) {
                ReadStatus status = new ReadStatus(userId, channel.getId(), Instant.now());
                readStatusRepository.save(status);
                createdStatuses.add(status);
            }
            return createChannelResult(channel);
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

    // ID로 채널을 조회하고 application 결과로 변환하여 반환
    @Override
    public ChannelResult find(UUID id) {
        return createChannelResult(getChannel(id));
    }

    // 사용자가 볼 수 있는 모든 채널을 조회 (PUBLIC 채널 전체 + 참여 중인 PRIVATE 채널)
    // 채널마다 참여자를 다시 조회하면 목록 한 번이 조회 C회로 늘어나므로(N+1),
    // ReadStatus를 한 번만 읽어 참여 채널 판별과 참여자 목록 조립에 함께 사용한다.
    @Override
    public List<ChannelResult> findAllByUserId(UUID userId) {
        requireUserExists(userId);

        List<ReadStatus> allStatuses = readStatusRepository.findAll();
        // 채널별 참여자 목록 (PRIVATE 채널 응답 조립용)
        Map<UUID, List<UUID>> participantIdsByChannelId = allStatuses.stream()
                .collect(Collectors.groupingBy(
                        ReadStatus::getChannelId,
                        Collectors.mapping(ReadStatus::getUserId, Collectors.toList())
                ));
        // set을 이용해 채널 id 중복을 없게 하기 + 등록된 ReadStatus를 통해 userid로 channelId 찾기
        Set<UUID> participatedChannelIds = allStatuses.stream()
                .filter(status -> status.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        List<Channel> visibleChannels = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC
                        || participatedChannelIds.contains(channel.getId()))
                .toList();
        // 마지막 메시지 시각도 채널마다 묻지 않고 한 번에 구한다.
        Map<UUID, Instant> lastMessageAtByChannelId = messageRepository
                .findLastMessageAtByChannelIdIn(visibleChannels.stream().map(Channel::getId).toList())
                .stream()
                .collect(Collectors.toMap(
                        ChannelLastMessageAt::getChannelId,
                        ChannelLastMessageAt::getLastMessageAt
                ));

        return visibleChannels.stream()
                .map(channel -> createChannelResult(
                        channel,
                        participantIdsByChannelId.getOrDefault(channel.getId(), List.of()),
                        lastMessageAtByChannelId.get(channel.getId())
                ))
                .toList();
    }

    // 채널 정보 수정 (요청에 없는 필드는 기존 값 유지)
    @Override
    public ChannelResult update(UUID id, UpdatePublicChannelCommand command) {
        Channel channel = getChannel(id);
        UpdatePublicChannelCommand target = Objects.requireNonNull(command);
        channel.update(
                target.newName() == null ? channel.getName() : target.newName(),
                target.newDescription() == null
                        ? channel.getDescription()
                        : target.newDescription()
        );
        return createChannelResult(channelRepository.save(channel));
    }

    // 첨부 목록(지연 로딩)을 읽고 파일 삭제를 커밋 뒤로 미루려면 트랜잭션이 필요하다.
    @Override
    @Transactional
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new EntityNotFoundException(Channel.class, id);
        }
        // 채널을 참조하는 읽음 상태와 메시지를 먼저 지우고 채널을 지운다.
        readStatusRepository.deleteAllByChannelId(id);
        deleteMessages(id);
        channelRepository.deleteById(id);
    }

    // 채널의 메시지를 지운다. 메시지에 달린 첨부파일도 함께 지운다.
    private void deleteMessages(UUID channelId) {
        for (Message message : messageRepository.findAllByChannelId(channelId)) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
                fileManager.deleteAfterCommit(attachmentId); // 실제 파일은 커밋이 확정된 뒤 삭제한다
            }
            messageRepository.deleteById(message.getId());
        }
    }

    // 사용자가 존재하는지 확인하고, 없으면 예외를 던지는 헬퍼 메서드
    private void requireUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(User.class, userId);
        }
    }

    // ID로 채널을 조회하고, 없으면 예외를 던지는 헬퍼 메서드
    private Channel getChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Channel.class, id));
    }

    // 단건 조회 경로: 해당 채널의 ReadStatus와 마지막 메시지 시각만 조회한다.
    private ChannelResult createChannelResult(Channel channel) {
        List<UUID> participantIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList()
                : List.of();
        Instant lastMessageAt = messageRepository
                .findLastMessageAtByChannelIdIn(List.of(channel.getId()))
                .stream()
                .findFirst()
                .map(ChannelLastMessageAt::getLastMessageAt)
                .orElse(null); // 메시지가 없으면 null
        return createChannelResult(channel, participantIds, lastMessageAt);
    }

    // Channel 엔티티를 application 결과로 변환하는 헬퍼 메서드.
    // 조회 전략은 호출 경로에 따라 다르지만 변환 규칙은 이 메서드 한 벌로 유지한다.
    private ChannelResult createChannelResult(Channel channel, List<UUID> participantIds, Instant lastMessageAt) {
        // private면 참여자 ID 받고 public이면 빈값을 보낸다.
        return ChannelResult.from(
                channel,
                channel.getType() == ChannelType.PRIVATE ? participantIds : List.of(),
                lastMessageAt
        );
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
