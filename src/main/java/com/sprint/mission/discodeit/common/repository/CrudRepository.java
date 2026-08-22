package com.sprint.mission.discodeit.common.repository;

import com.sprint.mission.discodeit.common.entity.Identifiable;

import java.util.List;
import java.util.UUID;

/**
 * 기본적인 CRUD(생성/조회/수정/삭제) 기능을 정의하는 공통 리포지토리 인터페이스.
 * 이 인터페이스 덕분에 Service 계층은 데이터가 파일에 저장되는지, 메모리에 저장되는지 등
 * 구체적인 저장 방식을 몰라도 된다 (의존성 역전 원칙).
 *
 * @param <T> 저장할 엔티티 타입. Identifiable을 구현해야 하므로 반드시 getId()를 가진다.
 */
// 설계: 인터페이스가 저장 계약을 소유하므로 Service는 File/JCF 구현 선택을 알 필요가 없다.
// 문법: T는 Identifiable 구현 타입만 받을 수 있는 제네릭 타입 매개변수다.
public interface CrudRepository<T extends Identifiable> {

    // 새로운 엔티티를 저장소에 저장한다
    T create(T entity);

    // ID 단건 조회는 반드시 존재해야 하므로 get으로 필수 조회 계약을 드러낸다.
    // 들어오는 ID에 대해 계약 상 존재 보장 조회를 보장했음을 정의했다 가정
    // 만약 해당 ID의 엔티티가 없으면 예외가 발생한다
    T getById(UUID id);

    // 해당 ID의 엔티티가 존재하는지 여부를 반환한다
    boolean existsById(UUID id);

    // 저장소에 있는 모든 엔티티를 리스트로 반환한다
    List<T> findAll();

    // 기존 엔티티를 새로운 상태로 갱신한다
    T update(T entity);

    // 해당 ID의 엔티티를 저장소에서 삭제한다
    void deleteById(UUID id);
}
