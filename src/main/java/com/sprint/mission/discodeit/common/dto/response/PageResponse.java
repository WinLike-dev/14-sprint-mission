package com.sprint.mission.discodeit.common.dto.response;

import java.util.List;

/**
 * 페이지 응답 공통 DTO.
 * 어떤 목록이든 같은 모양으로 내보내려고 제네릭으로 둔다.
 *
 * @param content       실제 데이터
 * @param number        페이지 번호 (0부터)
 * @param size          페이지 크기
 * @param hasNext       다음 페이지가 있는지
 * @param totalElements 전체 개수. 셀 필요가 없으면 null이다.
 */
public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        boolean hasNext,
        Long totalElements
) {

    public PageResponse {
        content = List.copyOf(content);
    }
}
