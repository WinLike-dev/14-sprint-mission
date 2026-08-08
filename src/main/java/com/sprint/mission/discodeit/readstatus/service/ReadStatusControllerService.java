package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.dto.response.ReadStatusDto;

import java.util.List;
import java.util.UUID;

public interface ReadStatusControllerService {

    ReadStatusDto create(ReadStatusCreateRequest request);

    ReadStatusDto find(UUID id);

    List<ReadStatusDto> findAllByUserId(UUID userId);

    ReadStatusDto update(UUID id, ReadStatusUpdateRequest request);

    void delete(UUID id);
}
