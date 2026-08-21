package com.sprint.mission.discodeit.common.repository.file;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 저장 파일 하나당 잠금 하나를 나눠주는 곳.
 *
 * 파일 저장소를 쓰는 동안만 필요한 장치다. 데이터베이스로 옮기면 같은 일을
 * 트랜잭션이 대신하므로 이 클래스도 함께 사라진다.
 *
 * 잠금은 파일 경로로 구분한다. 서로 다른 엔티티를 동시에 다루는 요청은
 * 서로를 기다리지 않고, 같은 엔티티를 다루는 요청만 차례를 기다린다.
 * 재진입 가능한 잠금이라 같은 스레드가 중첩해서 얻어도 막히지 않는다.
 */
public class FileLockProvider {

    private final Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(Path path) {
        return locks.computeIfAbsent(path, key -> new ReentrantLock());
    }
}
