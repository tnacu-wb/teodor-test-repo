package uk.co.whitbread.hotel.account.config;

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

  @Value("${threadpool.cdh.coreSize}")
  private int cdhCoreSize;
  @Value("${threadpool.cdh.maxSize}")
  private int cdhMaxSize;
  @Value("${threadpool.cdh.queueSize}")
  private int cdhQueueCapacity;

  @Bean("cdhExecutor")
  public Executor cdhExecutor() {

    log.info("Initialize cdh pool with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", cdhCoreSize, cdhMaxSize, cdhQueueCapacity);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(cdhCoreSize);
    executor.setMaxPoolSize(cdhMaxSize);
    executor.setQueueCapacity(cdhQueueCapacity);
    executor.setThreadNamePrefix("cdh-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
