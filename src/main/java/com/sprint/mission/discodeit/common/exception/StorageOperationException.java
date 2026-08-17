package com.sprint.mission.discodeit.common.exception;

import java.util.UUID;

/**
 * 파일 읽기/쓰기 등 저장소(Storage) 관련 작업이 실패했을 때 발생하는 예외.
 * 비즈니스 로직의 문제가 아니라 인프라(파일 시스템, DB 등) 수준의 오류를 나타낸다.
 * GlobalExceptionHandler에서 이 예외를 잡아 500 Internal Server Error로 변환한다.
 */
// 저장 기술 경계의 실패를 호출자가 도메인 부재와 구분할 수 있게 하는 이름 있는 예외다.
public class StorageOperationException extends RuntimeException {

    // operation: 어떤 작업인지 (예: "save", "delete")
    // stage: 작업의 어떤 단계에서 실패했는지 (예: "serialize", "write")
    // entityType: 어떤 엔티티에 대한 작업인지
    // id: 해당 엔티티의 ID (null일 수 있음)
    // cause: 실제 발생한 원인 예외
    public StorageOperationException(
            String operation,
            String stage,
            Class<?> entityType,
            UUID id,
            Throwable cause
    ) {
        super(
                "저장 작업에 실패했습니다. operation=%s, stage=%s, %s"
                        .formatted(operation, stage, dataContext(entityType, id)),
                cause
        );
    }

    // 에러 메시지에 포함할 엔티티 정보를 생성한다. ID가 없으면 타입만 표시한다.
    private static String dataContext(Class<?> entityType, UUID id) {
        return id == null
                ? "type=" + entityType.getSimpleName()
                : "type=%s, id=%s".formatted(entityType.getSimpleName(), id);
    }
}
