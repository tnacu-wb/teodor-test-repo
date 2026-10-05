package uk.co.whitbread.piba.account.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ThreadPoolExecutorsConfig {

  @Value("${threadpool.worldline.coreSize}")
  private int wlCoreSize;
  @Value("${threadpool.worldline.maxSize}")
  private int wlMaxSize;
  @Value("${threadpool.worldline.queueSize}")
  private int wlQueueCapacity;

  @Bean("worldLineExecutor")
  public Executor pibaAccountExecutor() {

    log.info("Initialize worldline pool with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", wlCoreSize, wlMaxSize, wlQueueCapacity);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(wlCoreSize);
    executor.setMaxPoolSize(wlMaxSize);
    executor.setQueueCapacity(wlQueueCapacity);
    executor.setThreadNamePrefix("worldline-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
