package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.service.dto.result.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.entity.ReadStatus;
import com.sprint.mission.discodeit.common.exception.exceptions.DuplicateRequestValueException;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.channel.repository.ReadStatusRepository.ChannelParticipant;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.content.storage.BinaryContentFileManager;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository.ChannelLastMessageAt;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.service.dto.result.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 읽기는 클래스에 걸린 readOnly 트랜잭션에서, 쓰기는 메서드의 @Transactional에서 처리한다.
 */
@Service
@RequiredArgsConstructor
// 기본은 읽기 전용 트랜잭션이다. 쓰기 메서드만 @Transactional로 덮어쓴다.
@Transactional(readOnly = true)
public class ChannelServiceImpl implements ChannelControllerService {

    private final ChannelRepository channelRepository; // 채널 저장소
    private final ReadStatusRepository readStatusRepository; // 읽음 상태 저장소
    private final UserRepository userRepository; // 사용자 존재 확인용
    private final MessageRepository messageRepository; // 마지막 메시지 시각 조회, 채널 삭제 시 메시지 정리용
    private final BinaryContentFileManager fileManager; // 채널 삭제 시 첨부파일의 실제 파일 정리용

    // 퍼블릭 채널 생성 -> 바로 저장
    @Override
    @Transactional
    public ChannelResult createPublic(CreatePublicChannelCommand command) {
        CreatePublicChannelCommand target = Objects.requireNonNull(command);
        Channel channel = Channel.publicChannel(target.name(), target.description());
        return createChannelResult(channelRepository.save(channel));
    }

    // 프라이빗 채널 생성 -> 1. 참여자 명단 올바른 지 체크 2. 채널 생성 3. 읽기상태 생성
    // 참여자 중 한 명이라도 저장에 실패하면 트랜잭션이 롤백되어 채널까지 함께 사라진다.
    @Override
    @Transactional
    public ChannelResult createPrivate(CreatePrivateChannelCommand command) {
        List<UUID> participantIds = Objects.requireNonNull(command).participantIds();
        // set을 이용해 중복을 없게 하고, 입력 순서 유지시키기 (큰 의미는 모르겠지만 일단 디코에는 그렇게 구현되니)
        Set<UUID> uniqueParticipantIds = new LinkedHashSet<>(participantIds);
        if (uniqueParticipantIds.size() != participantIds.size()) {
            throw new DuplicateRequestValueException(Channel.class, "participantIds");
        }
        // 멤버체크 exception
        uniqueParticipantIds.forEach(this::requireUserExists);

        // 저장하면 @PrePersist가 createdAt을 채운다.
        Channel channel = channelRepository.save(Channel.privateChannel());
        // 존재는 위에서 확인했으므로 SELECT 없이 프록시 참조만 얻는다.
        // findById로 불러오면 지연 로딩이 안 되는 User.status까지 조회가 따라온다.
        // 과제 베이스 코드 변경: 참여자의 마지막 읽음 시각은 채널이 만들어진 시각에서 시작한다.
        List<ReadStatus> statuses = uniqueParticipantIds.stream()
                .map(userId -> new ReadStatus(
                        userRepository.getReferenceById(userId), channel, channel.getCreatedAt()
                ))
                .toList();
        readStatusRepository.saveAll(statuses);
        return createChannelResult(channel);
    }

    // ID로 채널을 조회하고 application 결과로 변환하여 반환
    @Override
    public ChannelResult find(UUID id) {
        return createChannelResult(getChannel(id));
    }

    // 사용자가 볼 수 있는 모든 채널을 조회 (PUBLIC 채널 전체 + 참여 중인 PRIVATE 채널)
    // 참여 채널 id -> 볼 수 있는 채널 -> 참여자 -> 마지막 메시지 시각 순으로,
    // 필요한 행만 네 번의 쿼리로 모은다. 채널마다 되묻지도(N+1), 전체를 읽어 거르지도 않는다.
    @Override
    public List<ChannelResult> findAllByUserId(UUID userId) {
        requireUserExists(userId);

        List<UUID> participatedChannelIds = readStatusRepository.findChannelIdsByUserId(userId);
        // 참여 중인 PRIVATE 채널이 없으면 조건을 하나 줄인다.
        List<Channel> visibleChannels = participatedChannelIds.isEmpty()
                ? channelRepository.findAllByType(ChannelType.PUBLIC)
                : channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, participatedChannelIds);

        // 참여자 목록은 PRIVATE 채널 응답에만 들어가므로 그 채널들만 묻는다.
        Map<UUID, List<UserResult>> participantsByChannelId = findParticipants(
                visibleChannels.stream()
                        .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                        .map(Channel::getId)
                        .toList()
        );
        Map<UUID, Instant> lastMessageAtByChannelId = findLastMessageAt(
                visibleChannels.stream().map(Channel::getId).toList()
        );

        return visibleChannels.stream()
                .map(channel -> createChannelResult(
                        channel,
                        participantsByChannelId.getOrDefault(channel.getId(), List.of()),
                        lastMessageAtByChannelId.get(channel.getId())
                ))
                .toList();
    }

    // 채널 정보 수정 (요청에 없는 필드는 기존 값 유지)
    @Override
    @Transactional
    public ChannelResult update(UUID id, UpdatePublicChannelCommand command) {
        Channel channel = getChannel(id);
        UpdatePublicChannelCommand target = Objects.requireNonNull(command);
        channel.update(
                target.newName() == null ? channel.getName() : target.newName(),
                target.newDescription() == null
                        ? channel.getDescription()
                        : target.newDescription()
        );
        // 영속 상태라 변경 감지로 반영되므로 save를 부르지 않는다.
        return createChannelResult(channel);
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

    // 채널의 메시지를 지운다. 첨부 행은 cascade REMOVE로 함께 지워지고, 실제 파일만 따로 정리한다.
    private void deleteMessages(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<UUID> attachmentIds = messages.stream()
                .flatMap(message -> message.getAttachments().stream())
                .map(BinaryContent::getId)
                .toList();
        messageRepository.deleteAll(messages);
        fileManager.deleteAllAfterCommit(attachmentIds); // 실제 파일은 커밋이 확정된 뒤 삭제한다
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

    // 단건 조회 경로: 목록과 같은 조회를 채널 하나에 대해서만 수행한다.
    private ChannelResult createChannelResult(Channel channel) {
        List<UUID> channelIds = List.of(channel.getId());
        List<UserResult> participants = channel.getType() == ChannelType.PRIVATE
                ? findParticipants(channelIds).getOrDefault(channel.getId(), List.of())
                : List.of();
        // 메시지가 없으면 결과에 없으므로 null이 된다.
        Instant lastMessageAt = findLastMessageAt(channelIds).get(channel.getId());
        return createChannelResult(channel, participants, lastMessageAt);
    }

    // 채널별 참여자를 한 번에 모은다. 대상이 없으면 쿼리를 보내지 않는다.
    // 읽음 상태에서 (채널, 사용자) 짝을 얻고, 사용자만 따로 한 번에 조회해 붙인다.
    private Map<UUID, List<UserResult>> findParticipants(List<UUID> channelIds) {
        if (channelIds.isEmpty()) {
            return Map.of();
        }
        List<ChannelParticipant> participants =
                readStatusRepository.findParticipantsByChannelIdIn(channelIds);
        if (participants.isEmpty()) {
            return Map.of();
        }
        Map<UUID, UserResult> resultsByUserId = userRepository.findAllByIdIn(
                        participants.stream()
                                .map(ChannelParticipant::getUserId)
                                .distinct()
                                .toList()
                ).stream()
                .collect(Collectors.toMap(User::getId, UserResult::from));

        return participants.stream()
                .collect(Collectors.groupingBy(
                        ChannelParticipant::getChannelId,
                        Collectors.mapping(
                                participant -> resultsByUserId.get(participant.getUserId()),
                                Collectors.toList()
                        )
                ));
    }

    // 채널별 마지막 메시지 시각을 한 번에 모은다. 메시지가 없는 채널은 결과에 없다.
    private Map<UUID, Instant> findLastMessageAt(List<UUID> channelIds) {
        if (channelIds.isEmpty()) {
            return Map.of();
        }
        return messageRepository.findLastMessageAtByChannelIdIn(channelIds).stream()
                .collect(Collectors.toMap(
                        ChannelLastMessageAt::getChannelId,
                        ChannelLastMessageAt::getLastMessageAt
                ));
    }

    // Channel 엔티티를 application 결과로 변환하는 헬퍼 메서드.
    // 조회 전략은 호출 경로에 따라 다르지만 변환 규칙은 이 메서드 한 벌로 유지한다.
    private ChannelResult createChannelResult(
            Channel channel,
            List<UserResult> participants,
            Instant lastMessageAt
    ) {
        // private면 참여자를 담고 public이면 빈값을 보낸다.
        return ChannelResult.from(
                channel,
                channel.getType() == ChannelType.PRIVATE ? participants : List.of(),
                lastMessageAt
        );
    }
}
