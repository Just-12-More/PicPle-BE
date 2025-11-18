package com.Just_112_More.PicPle.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean(name = "photoWorkerExecutor")
    public ThreadPoolTaskExecutor photoWorkerExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);        // worker 4개
        executor.setMaxPoolSize(4);         // 늘리지 않음 (안정성)
        executor.setQueueCapacity(2000);    // bounded queue
        executor.setThreadNamePrefix("photo-upload-worker-");
        executor.initialize();

        return executor;
    }
}
