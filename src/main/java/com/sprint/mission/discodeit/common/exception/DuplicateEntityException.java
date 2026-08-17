package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

/**
 * 저장하려는 식별자가 이미 저장소에 있을 때 발생한다.
 *
 * 식별자는 UUID.randomUUID()로 만들어지므로, 이 예외가 뜬다는 것은 호출자가 잘못했다는
 * 뜻이 아니라 식별자 생성이나 저장 흐름에 문제가 있다는 뜻이다.
 * 예전에는 DuplicateDataException을 상속해 409로 나갔는데, 그러면 클라이언트에게
 * "다시 시도하면 될 수도 있는 충돌"처럼 보인다. 실제로는 클라이언트가 고칠 수 없다.
 *
 * 그래서 DuplicateDataException 계열에서 떼어냈다. 전역 처리기의 마지막 단계가
 * 예상하지 못한 예외로 받아 500으로 응답하고 원인을 로그에 남긴다.
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(Class<?> entityType, UUID id) {
        super(
                "이미 존재하는 엔티티입니다. type=%s, id=%s"
                        .formatted(entityType.getSimpleName(), id)
        );
    }
}
