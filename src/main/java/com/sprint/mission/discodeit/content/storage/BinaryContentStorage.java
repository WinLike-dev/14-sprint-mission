package com.sprint.mission.discodeit.content.storage;

import com.sprint.mission.discodeit.content.service.dto.BinaryContentResult;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

/**
 * BinaryContent의 실제 바이너리 데이터를 저장하고 읽는 저장소.
 * 메타 정보(파일명, 크기, 유형)는 BinaryContentRepository가 DB에 저장하고,
 * 바이트 데이터는 이 인터페이스의 구현체가 로컬 디스크나 원격 저장소에 둔다.
 */
public interface BinaryContentStorage {

    // BinaryContent id를 키로 바이트 데이터를 저장하고, 그 키를 돌려준다.
    UUID put(UUID binaryContentId, byte[] bytes);

    // BinaryContent id로 저장된 데이터를 읽는다. 스트림은 호출자가 닫는다.
    InputStream get(UUID binaryContentId);

    // 메타 정보와 저장된 데이터로 파일 다운로드 응답을 만든다.
    ResponseEntity<?> download(BinaryContentResult binaryContent);
}
