package com.Just_112_More.PicPle.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoWorkerMetrics {
    @Qualifier("photoWorkerExecutor")
    private final ThreadPoolTaskExecutor executor;

    @Scheduled(fixedRate = 2000) // 2초마다 로그기록
    public void logMetrics() {
        ThreadPoolExecutor pool = executor.getThreadPoolExecutor();

        log.info("[PHOTO-WORKER] active={}, poolSize={}, queued={}, completed={}",
                pool.getActiveCount(),
                pool.getPoolSize(),
                pool.getQueue().size(),
                pool.getCompletedTaskCount()
        );
    }
}
