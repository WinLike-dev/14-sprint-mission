package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalBinaryContentServiceImpl implements InternalBinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentDto create(BinaryContentCreateRequest request) {
        BinaryContentCreateRequest target = Objects.requireNonNull(request);
        BinaryContent content = new BinaryContent(
                target.fileName(), target.contentType(), target.bytes()
        );
        return BinaryContentDto.from(binaryContentRepository.create(content));
    }
    // 예외처리
    @Override
    public BinaryContentDto find(UUID id) {
        return BinaryContentDto.from(binaryContentRepository.getById(id));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(List.copyOf(ids)).stream()
                .map(BinaryContentDto::from)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.deleteById(id);
    }
}
