package com.sprint.mission.discodeit.content.application.port.out;

import com.sprint.mission.discodeit.content.domain.binarycontent.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 바이너리 콘텐츠 저장소 outbound 포트.
 * 여러 ID로 한 번에 조회하는 쿼리를 추가로 정의한다.
 */
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    // 여러 개의 ID에 해당하는 BinaryContent를 한 번의 IN 쿼리로 조회한다.
    // 없는 ID는 결과에서 빠질 뿐 예외가 나지 않는다.
    List<BinaryContent> findAllByIdIn(Collection<UUID> ids);
}
