package com.sprint.mission.discodeit.common.exception;

/**
 * 엔티티 간의 연관 관계가 이미 존재할 때 발생하는 예외.
 * 예: 이미 특정 채널에 참여 중인 사용자가 다시 참여하려 할 때 등.
 * DuplicateDataException을 상속하므로 GlobalExceptionHandler에서 409 Conflict로 처리된다.
 */
public class DuplicateAssociationException extends DuplicateDataException {

    // entityType: 중복 연관이 발생한 엔티티 클래스
    // association: 어떤 연관 관계에서 중복이 발생했는지에 대한 설명
    public DuplicateAssociationException(Class<?> entityType, String association) {
        super(
                "중복된 연관 관계입니다. type=%s, association=%s"
                        .formatted(entityType.getSimpleName(), association)
        );
    }
}
