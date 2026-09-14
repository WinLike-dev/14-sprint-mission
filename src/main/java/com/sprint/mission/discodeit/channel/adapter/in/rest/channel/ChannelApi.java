package com.sprint.mission.discodeit.channel.adapter.in.rest.channel;

import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.adapter.in.rest.channel.dto.response.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Public Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    ResponseEntity<ChannelDto> createPublic(PublicChannelCreateRequest request);

    @Operation(summary = "Private Channel 생성")
    @ApiResponse(
            responseCode = "201",
            description = "Private Channel이 성공적으로 생성됨",
            content = @Content(schema = @Schema(implementation = ChannelDto.class))
    )
    ResponseEntity<ChannelDto> createPrivate(PrivateChannelCreateRequest request);

    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공")
    ResponseEntity<List<ChannelDto>> findAllByUserId(
            @Parameter(description = "조회할 User ID") UUID userId
    );

    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<ChannelDto> update(
            @Parameter(description = "수정할 Channel ID") UUID channelId,
            PublicChannelUpdateRequest request
    );

    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel이 성공적으로 삭제됨",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 Channel ID") UUID channelId
    );
}
