package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.controller.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.service.dto.command.LoginCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthRestMapper {

    LoginCommand toCommand(LoginRequest request);
}
