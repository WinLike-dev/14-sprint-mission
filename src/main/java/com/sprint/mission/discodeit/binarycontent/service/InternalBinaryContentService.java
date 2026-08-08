package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;

import java.util.List;
import java.util.UUID;

// 설계: Internal 접두사는 다른 Service의 내부 협력에만 사용하는 경계임을 나타낸다.
public interface InternalBinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto find(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}
