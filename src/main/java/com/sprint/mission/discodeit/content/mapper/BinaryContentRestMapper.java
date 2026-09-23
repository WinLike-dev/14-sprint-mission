package com.sprint.mission.discodeit.content.mapper;

import com.sprint.mission.discodeit.content.controller.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.content.service.dto.result.BinaryContentResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BinaryContentRestMapper {

    BinaryContentDto toResponse(BinaryContentResult result);

    List<BinaryContentDto> toResponses(List<BinaryContentResult> results);
}
