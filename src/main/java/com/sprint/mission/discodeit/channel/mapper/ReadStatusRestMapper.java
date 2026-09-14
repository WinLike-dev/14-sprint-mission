package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.service.dto.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.service.dto.ReadStatusResult;
import com.sprint.mission.discodeit.channel.service.dto.UpdateReadStatusCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ReadStatusRestMapper {

    CreateReadStatusCommand toCommand(ReadStatusCreateRequest request);

    @Mapping(target = "lastReadAt", source = "newLastReadAt")
    UpdateReadStatusCommand toCommand(ReadStatusUpdateRequest request);

    ReadStatusDto toResponse(ReadStatusResult result);

    List<ReadStatusDto> toResponses(List<ReadStatusResult> results);
}
