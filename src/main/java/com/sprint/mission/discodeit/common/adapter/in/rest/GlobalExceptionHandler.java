package com.sprint.mission.discodeit.common.adapter.in.rest;

import com.sprint.mission.discodeit.channel.domain.channel.exception.UnsupportedChannelOperationException;
import com.sprint.mission.discodeit.channel.domain.readstatus.exception.ReadStatusCreationNotAllowedException;
import com.sprint.mission.discodeit.common.exception.DuplicateDataException;
import com.sprint.mission.discodeit.common.exception.EntityNotFoundException;
import com.sprint.mission.discodeit.common.exception.InvalidValueException;
import com.sprint.mission.discodeit.common.exception.StorageOperationException;
import com.sprint.mission.discodeit.user.application.auth.exception.AuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 애플리케이션 전역 예외 처리기.
 * 예외 타입을 HTTP 상태로 옮기고, 응답 본문 형태를 한 벌로 유지한다.
 *
 * 상태를 고를 때의 기준:
 * - 400 : 요청 자체가 잘못됨. 서버 상태와 무관하게 항상 거부된다.
 * - 401 : 인증 실패
 * - 404 : 대상 리소스가 없음
 * - 409 : 요청은 올바르지만 현재 서버 상태와 충돌
 * - 500 : 서버 잘못. 클라이언트가 고칠 수 없다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 클라이언트에게 내부 사정을 알리지 않기 위한 고정 문구
    private static final String INTERNAL_MESSAGE = "서버에서 요청을 처리하지 못했습니다.";

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

    // 도메인 규칙을 만족하지 못한 값 -> 400. 호출자가 고칠 수 있는 실패이므로 메시지를 그대로 전달한다.
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidValue(InvalidValueException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 경로 변수나 쿼리 파라미터의 타입이 맞지 않을 때 -> 400.
    // 내부 변환 예외 메시지를 그대로 노출하지 않고 어떤 값이 문제인지만 알린다.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "%s 값의 형식이 올바르지 않습니다.".formatted(e.getName())
        );
    }

    // 본문을 읽을 수 없을 때(깨진 JSON 등) -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다.");
    }

    // 필수 쿼리 파라미터가 없을 때 -> 400
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(
            MissingServletRequestParameterException e
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "%s 파라미터가 필요합니다.".formatted(e.getParameterName())
        );
    }

    // 파일/DB 등 저장소 관련 작업 실패 시 -> 500 Internal Server Error 응답
    @ExceptionHandler(StorageOperationException.class)
    public ResponseEntity<Map<String, Object>> handleStorageOperation(StorageOperationException e) {
        log.error("저장소 작업이 실패했습니다.", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_MESSAGE);
    }

    // InvalidValueException으로 분류되지 않은 잘못된 인자는 코드의 결함으로 본다.
    // 클라이언트가 고칠 수 없는 실패이므로 400이 아니라 500이고, 내부 메시지는 로그에만 남긴다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("처리되지 않은 잘못된 인자입니다.", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_MESSAGE);
    }

    // 에러 응답 JSON 본문을 공통 형식으로 생성하는 헬퍼 메서드
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString()); // 에러 발생 시각
        body.put("status", status.value());              // HTTP 상태 코드 숫자 (예: 404)
        body.put("error", status.getReasonPhrase());     // HTTP 상태 코드 이름 (예: "Not Found")
        body.put("message", message);                    // 구체적인 에러 메시지
        return ResponseEntity.status(status).body(body);
    }
}
