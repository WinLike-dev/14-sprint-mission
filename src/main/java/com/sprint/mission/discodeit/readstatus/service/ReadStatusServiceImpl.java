package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.common.exception.DuplicateAssociationException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusControllerService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto create(UUID userId, UUID channelId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        Objects.requireNonNull(
                channelId,
                "channelId는 null일 수 없습니다."
        );
        userRepository.getById(userId);
        channelRepository.getById(channelId);

        // 유저와 채널 id 모두 똑같은 놈 있는 지 체크
        if (readStatusRepository.findByUserIdAndChannelId(
                userId, channelId
        ).isPresent()) {
            throw new DuplicateAssociationException(
                    ReadStatus.class,
                    associationContext(userId, channelId)
            );
        }
        ReadStatus status = new ReadStatus(userId, channelId);
        return ReadStatusDto.from(readStatusRepository.create(status));
    }

    @Override
    public ReadStatusDto find(UUID userId, UUID channelId) {
        return ReadStatusDto.from(getByUserIdAndChannelId(userId, channelId));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
        userRepository.getById(userId);
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto::from)
                .toList();
    }

    @Override
    public ReadStatusDto updateLastReadAt(UUID userId, UUID channelId) {
        ReadStatus status = getByUserIdAndChannelId(userId, channelId);
        status.updateLastReadAt();
        return ReadStatusDto.from(readStatusRepository.update(status));
    }

    @Override
    public void delete(UUID userId, UUID channelId) {
        ReadStatus status = getByUserIdAndChannelId(userId, channelId);
        readStatusRepository.deleteById(status.getId());
    }

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

    private String associationContext(UUID userId, UUID channelId) {
        return "userId=%s, channelId=%s".formatted(userId, channelId);
    }
}
