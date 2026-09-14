package com.sprint.mission.discodeit.content.service;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;
import com.sprint.mission.discodeit.content.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.content.entity.BinaryContent;
import com.sprint.mission.discodeit.common.exception.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * ContentControllerService의 구현체.
 * 바이너리 콘텐츠 조회의 실제 로직을 담당한다.
 * 도메인 엔티티를 애플리케이션 결과 모델로 변환하여 반환한다.
 */
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentControllerService {

    private final BinaryContentRepository binaryContentRepository;

    // ID로 바이너리 콘텐츠를 조회하고 결과 모델로 변환하여 반환한다
    @Override
    public BinaryContentResult find(UUID id) {
        return BinaryContentResult.from(
                binaryContentRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(BinaryContent.class, id))
        );
    }

    // 여러 ID로 조회한 결과를 각각 DTO로 변환하여 리스트로 반환한다
    @Override
    public List<BinaryContentResult> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(List.copyOf(ids)).stream() // List.copyOf로 불변 복사
                .map(BinaryContentResult::from)
                .toList();
    }

}
