package com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent;

import com.sprint.mission.discodeit.content.adapter.in.rest.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.content.application.binarycontent.dto.BinaryContentResult;
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
