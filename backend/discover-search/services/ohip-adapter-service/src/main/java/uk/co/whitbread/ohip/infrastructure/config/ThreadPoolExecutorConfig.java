package uk.co.whitbread.ohip.infrastructure.config;

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

  private final int availabilityCoreSize;
  private final int availabilityMaxSize;
  private final int availabilityQueueSize;
  private final int apiLimitsCoreSize;
  private final int apiLimitsMaxSize;
  private final int apiLimitsQueueSize;

  public ThreadPoolExecutorConfig(
      @Value("${threadpool.availability.coreSize}") int availabilityCoreSize,
      @Value("${threadpool.availability.maxSize}") int availabilityMaxSize,
      @Value("${threadpool.availability.queueSize}") int availabilityQueueSize,
      @Value("${threadpool.apiLimits.coreSize}") int apiLimitsCoreSize,
      @Value("${threadpool.apiLimits.maxSize}") int apiLimitsMaxSize,
      @Value("${threadpool.apiLimits.queueSize}") int apiLimitsQueueSize) {
    this.availabilityCoreSize = availabilityCoreSize;
    this.availabilityMaxSize = availabilityMaxSize;
    this.availabilityQueueSize = availabilityQueueSize;
    this.apiLimitsCoreSize = apiLimitsCoreSize;
    this.apiLimitsMaxSize = apiLimitsMaxSize;
    this.apiLimitsQueueSize = apiLimitsQueueSize;
  }

  @Bean("availabilityExecutor")
  public Executor availabilityExecutor() {

    log.info("Initialize availability pool with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", availabilityCoreSize, availabilityMaxSize, availabilityQueueSize);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(availabilityCoreSize);
    executor.setMaxPoolSize(availabilityMaxSize);
    executor.setQueueCapacity(availabilityQueueSize);
    executor.setThreadNamePrefix("availability-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  @Bean("apiLimitsExecutor")
  public Executor apiLimitsExecutor() {

    log.info("Initialize API limits pool with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", apiLimitsCoreSize, apiLimitsMaxSize, apiLimitsQueueSize);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(apiLimitsCoreSize);
    executor.setMaxPoolSize(apiLimitsMaxSize);
    executor.setQueueCapacity(apiLimitsQueueSize);
    executor.setThreadNamePrefix("api-limits-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

}
