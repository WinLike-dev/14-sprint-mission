package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.user.service.dto.UserStatusResult;
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
