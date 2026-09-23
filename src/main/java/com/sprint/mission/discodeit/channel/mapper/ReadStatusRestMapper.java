package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.controller.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.service.dto.command.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.service.dto.result.ReadStatusResult;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdateReadStatusCommand;
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
