package com.sprint.mission.discodeit.common.mapper;

import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

/**
 * Spring Data의 페이지 결과를 응답 DTO로 옮긴다.
 * 어떤 목록에도 쓸 수 있도록 제네릭 메서드로 둔다.
 */
@Component
public class PageResponseMapper {

    // 전체 개수를 세지 않는 조회. totalElements는 null이다.
    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext(),
                null
        );
    }

    // 전체 개수까지 세는 조회.
    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
