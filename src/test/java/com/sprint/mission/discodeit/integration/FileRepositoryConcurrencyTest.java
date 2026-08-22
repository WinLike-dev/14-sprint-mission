package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.common.repository.file.FileLockProvider;
import com.sprint.mission.discodeit.user.adapter.out.persistence.user.file.FileUserRepository;
import com.sprint.mission.discodeit.user.application.port.out.UserRepository;
import com.sprint.mission.discodeit.user.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 파일 저장소에 여러 요청이 동시에 들어오는 상황을 확인한다.
 * 프론트엔드를 붙이면 폴링 때문에 같은 파일을 동시에 건드리는 일이 실제로 생긴다.
 */
class FileRepositoryConcurrencyTest {

    private static final int THREADS = 16;
    private static final int ROUNDS = 20;

    // 같은 사용자를 여러 스레드가 동시에 읽고 쓴다.
    // 잠금이 없으면 읽는 쪽이 파일을 열고 있는 동안 쓰는 쪽의 교체가 실패하거나,
    // 존재 확인과 쓰기 사이에 삭제가 끼어들 수 있다.
    @Test
    void concurrentReadAndWriteOnSameEntityStaysConsistent(@TempDir Path root) throws Exception {
        UserRepository repository = new FileUserRepository(root, new FileLockProvider());
        User user = repository.create(new User("carol", "carol@example.com", "password", null));

        AtomicInteger failures = new AtomicInteger();
        List<Throwable> causes = new ArrayList<>();

        runConcurrently(round -> {
            for (int i = 0; i < ROUNDS; i++) {
                try {
                    User stored = repository.getById(user.getId());
                    assertNotNull(stored.getUsername());
                    repository.update(stored);
                } catch (RuntimeException exception) {
                    failures.incrementAndGet();
                    synchronized (causes) {
                        causes.add(exception);
                    }
                }
            }
            return null;
        });

        assertEquals(
                0,
                failures.get(),
                () -> "동시 접근이 실패했다: " + causes.stream().findFirst()
                        .map(Throwable::toString).orElse("")
        );
        assertEquals(user.getId(), repository.getById(user.getId()).getId());
    }

    // 서로 다른 사용자를 동시에 만든다. 잠금은 파일별이므로 서로를 기다리지 않는다.
    @Test
    void concurrentCreatesOfDifferentEntitiesAllSucceed(@TempDir Path root) throws Exception {
        UserRepository repository = new FileUserRepository(root, new FileLockProvider());

        runConcurrently(round -> {
            for (int i = 0; i < ROUNDS; i++) {
                repository.create(new User(
                        "user-" + round + "-" + i,
                        "user-%d-%d@example.com".formatted(round, i),
                        "password",
                        null
                ));
            }
            return null;
        });

        assertEquals(THREADS * ROUNDS, repository.findAll().size());
    }

    // 모든 스레드를 같은 지점에서 출발시켜 겹칠 확률을 높인다.
    private void runConcurrently(RoundTask task) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CyclicBarrier start = new CyclicBarrier(THREADS);
        List<Future<Void>> futures = new ArrayList<>();

        try {
            for (int thread = 0; thread < THREADS; thread++) {
                int round = thread;
                futures.add(executor.submit((Callable<Void>) () -> {
                    start.await(10, TimeUnit.SECONDS);
                    return task.run(round);
                }));
            }
            for (Future<Void> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    private interface RoundTask {
        Void run(int round) throws Exception;
    }
}
