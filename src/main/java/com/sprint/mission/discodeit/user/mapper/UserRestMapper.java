package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.common.exception.exceptions.UploadedFileReadException;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserDto;
import com.sprint.mission.discodeit.user.service.dto.CreateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UpdateUserCommand;
import com.sprint.mission.discodeit.user.service.dto.UserProfileCommand;
import com.sprint.mission.discodeit.user.service.dto.UserResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserRestMapper {

    // 나머지는 암시적 매핑이 되므로 profile만 작성
    @Mapping(target = "profile", source = "profile")
    CreateUserCommand toCommand(
            UserCreateRequest request,
            MultipartFile profile
    );

    @Mapping(target = "username", source = "request.newUsername")
    @Mapping(target = "email", source = "request.newEmail")
    @Mapping(target = "password", source = "request.newPassword")
    @Mapping(target = "profile", source = "profile")
    UpdateUserCommand toCommand(
            UserUpdateRequest request,
            MultipartFile profile
    );

    UserDto toResponse(UserResult result);

    List<UserDto> toResponses(List<UserResult> results);

    // MultipartFile은 REST 경계의 타입이므로 이 어댑터에서 애플리케이션 입력으로 바꾼다. ++ profile 불일치 타입 맞춰주기
    default UserProfileCommand toProfileCommand(MultipartFile profile) {
        if (profile == null || profile.isEmpty()) {
            return null;
        }
        try {
            return new UserProfileCommand(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        } catch (IOException exception) {
            throw new UploadedFileReadException(
                    "프로필 이미지를 읽지 못했습니다.",
                    exception
            );
        }
    }
}
