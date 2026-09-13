package com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus;

import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.readstatus.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.channel.application.readstatus.dto.CreateReadStatusCommand;
import com.sprint.mission.discodeit.channel.application.readstatus.dto.ReadStatusResult;
import com.sprint.mission.discodeit.channel.application.readstatus.dto.UpdateReadStatusCommand;
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
