package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelDto;
import com.sprint.mission.discodeit.channel.service.dto.ChannelResult;
import com.sprint.mission.discodeit.channel.service.dto.CreatePrivateChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.CreatePublicChannelCommand;
import com.sprint.mission.discodeit.channel.service.dto.UpdatePublicChannelCommand;
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

    UpdatePublicChannelCommand toCommand(PublicChannelUpdateRequest request);

    ChannelDto toResponse(ChannelResult result);

    List<ChannelDto> toResponses(List<ChannelResult> results);
}
