package com.edwin.trial_bank_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Without @EnableAsync, every @Async-annotated method (e.g. in
 * TransferNotificationListener) silently runs synchronously on the calling
 * thread instead of being dispatched to a separate pool — Spring does not
 * fail loudly here, it just quietly does nothing useful, which is a common
 * trap. This config turns @Async on and gives it a small dedicated pool
 * instead of using Spring's unbounded default, so a burst of transfers can't
 * spawn an unbounded number of threads.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notif-async-");
        executor.initialize();
        return executor;
    }
}
