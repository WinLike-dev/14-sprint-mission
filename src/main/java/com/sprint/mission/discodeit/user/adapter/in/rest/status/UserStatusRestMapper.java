package com.sprint.mission.discodeit.user.adapter.in.rest.status;

import com.sprint.mission.discodeit.user.adapter.in.rest.status.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.application.status.dto.UserStatusResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserStatusRestMapper {

    UserStatusDto toResponse(UserStatusResult result);

    List<UserStatusDto> toResponses(List<UserStatusResult> results);
}
