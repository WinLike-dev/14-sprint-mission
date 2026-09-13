package com.sprint.mission.discodeit.channel.adapter.in.rest.channel;

import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.application.channel.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.application.channel.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.application.channel.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.application.channel.dto.UpdatePublicChannelCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ChannelRestMapper {

    CreatePublicChannelCommand toCommand(PublicChannelCreateRequest request);

    CreatePrivateChannelCommand toCommand(PrivateChannelCreateRequest request);

    @Mapping(target = "newName", source = "newName")
    @Mapping(target = "newDescription", source = "newDescription")
    UpdatePublicChannelCommand toCommand(PublicChannelUpdateRequest request);

    ChannelDto toResponse(ChannelResult result);

    List<ChannelDto> toResponses(List<ChannelResult> results);
}
