package com.sprint.mission.discodeit.common.adapter.in.rest;

import com.sprint.mission.discodeit.channel.domain.channel.exception.UnsupportedChannelOperationException;
import com.sprint.mission.discodeit.channel.domain.readstatus.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.common.exception.DuplicateDataException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.StorageOperationException;
import com.sprint.mission.discodeit.user.application.auth.exception.AuthenticationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

/**
 * REST inbound 어댑터의 전역 예외 처리기.
 * 컨트롤러에서 난 예외를 잡아 클라이언트에게 일관된 JSON 에러 응답을 반환한다.
 * 각 예외 타입에 맞는 HTTP 상태 코드(404, 409, 401 등)를 매핑해준다.
 *
 * @RestControllerAdvice: 모든 @RestController에 대해 공통으로 적용되는 예외 처리기임을 선언한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 엔티티를 찾을 수 없을 때 -> 404 Not Found 응답
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 중복 데이터가 존재할 때 -> 409 Conflict 응답
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateData(DuplicateDataException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    // 인증(로그인)에 실패했을 때 -> 401 Unauthorized 응답
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailed(AuthenticationFailedException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // 채널에서 지원하지 않는 작업을 시도했을 때 -> 400 Bad Request 응답
    @ExceptionHandler(UnsupportedChannelOperationException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedChannelOperation(
            UnsupportedChannelOperationException e
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 읽기 상태 생성이 허용되지 않을 때 -> 400 Bad Request 응답
    @ExceptionHandler(ReadStatusCreationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleReadStatusCreationNotAllowed(
            ReadStatusCreationNotAllowedException e
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 잘못된 인자(파라미터) 전달 시 -> 400 Bad Request 응답
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 파일/DB 등 저장소 관련 작업 실패 시 -> 500 Internal Server Error 응답
    @ExceptionHandler(StorageOperationException.class)
    public ResponseEntity<Map<String, Object>> handleStorageOperation(StorageOperationException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // 에러 응답 JSON 본문을 공통 형식으로 생성하는 헬퍼 메서드
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(), // 에러 발생 시각
                "status", status.value(),              // HTTP 상태 코드 숫자 (예: 404)
                "error", status.getReasonPhrase(),     // HTTP 상태 코드 이름 (예: "Not Found")
                "message", message                     // 구체적인 에러 메시지
        ));
    }
}
