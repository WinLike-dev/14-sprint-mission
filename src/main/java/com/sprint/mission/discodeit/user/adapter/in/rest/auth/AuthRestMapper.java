package com.sprint.mission.discodeit.user.adapter.in.rest.auth;

import com.sprint.mission.discodeit.user.adapter.in.rest.auth.dto.request.LoginRequest;
import com.sprint.mission.discodeit.user.application.auth.dto.LoginCommand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthRestMapper {

    LoginCommand toCommand(LoginRequest request);
}
