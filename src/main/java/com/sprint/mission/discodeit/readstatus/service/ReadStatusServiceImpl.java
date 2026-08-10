package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.common.exception.DuplicateAssociationException;
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
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        ReadStatusCreateRequest target = Objects.requireNonNull(request);
        userRepository.getById(target.userId());
        channelRepository.getById(target.channelId());
        if (readStatusRepository.findByUserIdAndChannelId(
                target.userId(), target.channelId()
        ).isPresent()) {
            throw new DuplicateAssociationException(
                    ReadStatus.class,
                    "userId=%s, channelId=%s".formatted(target.userId(), target.channelId())
            );
        }
        ReadStatus status = new ReadStatus(
                target.userId(), target.channelId(), target.lastReadAt()
        );
        return ReadStatusDto.from(readStatusRepository.create(status));
    }

    @Override
    public ReadStatusDto find(UUID id) {
        return ReadStatusDto.from(readStatusRepository.getById(id));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        userRepository.getById(userId);
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto::from)
                .toList();
    }

    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus status = readStatusRepository.getById(id);
        status.update(Objects.requireNonNull(request).lastReadAt());
        return ReadStatusDto.from(readStatusRepository.update(status));
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
