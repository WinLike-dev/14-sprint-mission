package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.controller.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.service.dto.result.UserStatusResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserStatusRestMapper {

    UserStatusDto toResponse(UserStatusResult result);
}
