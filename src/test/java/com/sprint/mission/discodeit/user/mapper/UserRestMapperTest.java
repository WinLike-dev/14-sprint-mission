package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.service.dto.CreateUserCommand;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class UserRestMapperTest {

    private final UserRestMapper mapper = Mappers.getMapper(UserRestMapper.class);

    @Test
    void convertsMultipartProfileToApplicationCommand() {
        UserCreateRequest request = new UserCreateRequest(
                "seungho",
                "seungho@example.com",
                "password"
        );
        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        CreateUserCommand command = mapper.toCommand(request, profile);

        assertThat(command.profile().fileName()).isEqualTo("profile.png");
        assertThat(command.profile().contentType()).isEqualTo("image/png");
        assertThat(command.profile().bytes()).containsExactly(1, 2, 3);
    }

    @Test
    void mapsMissingProfileToNull() {
        UserCreateRequest request = new UserCreateRequest(
                "seungho",
                "seungho@example.com",
                "password"
        );

        CreateUserCommand command = mapper.toCommand(request, null);

        assertThat(command.profile()).isNull();
    }
}
