package uk.co.whitbread.payapp.infrastructure.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ThreadPoolExecutorConfig {

  private int worldlineCoreSize;
  private int worldlineMaxSize;
  private int worldlineQueueCapacity;

  public ThreadPoolExecutorConfig(
      @Value("${threadpool.worldline.coreSize}") int worldlineCoreSize,
      @Value("${threadpool.worldline.maxSize}") int worldlineMaxSize,
      @Value("${threadpool.worldline.queueSize}") int worldlineQueueCapacity
  ) {
    this.worldlineCoreSize = worldlineCoreSize;
    this.worldlineMaxSize = worldlineMaxSize;
    this.worldlineQueueCapacity = worldlineQueueCapacity;
  }

  @Bean("worldlineExecutor")
  public Executor worldlineExecutor() {

    log.info("Initialize Worldline pool with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", worldlineCoreSize, worldlineMaxSize, worldlineQueueCapacity);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(worldlineCoreSize);
    executor.setMaxPoolSize(worldlineMaxSize);
    executor.setQueueCapacity(worldlineQueueCapacity);
    executor.setThreadNamePrefix("worldline-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
