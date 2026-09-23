package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.controller.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.controller.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.service.dto.result.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.command.UpdatePublicChannelCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ChannelRestMapper {

    CreatePublicChannelCommand toCommand(PublicChannelCreateRequest request);

    CreatePrivateChannelCommand toCommand(PrivateChannelCreateRequest request);

    UpdatePublicChannelCommand toCommand(PublicChannelUpdateRequest request);

    ChannelDto toResponse(ChannelResult result);

    List<ChannelDto> toResponses(List<ChannelResult> results);
}
