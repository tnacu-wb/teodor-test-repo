package uk.co.whitbread.piba.registration.config;

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

  @Value("${threadpool.reg.coreSize}")
  private int wlCoreSize;
  @Value("${threadpool.reg.maxSize}")
  private int wlMaxSize;
  @Value("${threadpool.reg.queueSize}")
  private int wlQueueCapacity;

  @Bean("pibaRegExecutor")
  public Executor pibaRegExecutor() {

    log.info("Initialize piba-reg-pool- with core size={}, "
        + "max size = {}, "
        + "queue capacity={}", wlCoreSize, wlMaxSize, wlQueueCapacity);

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(wlCoreSize);
    executor.setMaxPoolSize(wlMaxSize);
    executor.setQueueCapacity(wlQueueCapacity);
    executor.setThreadNamePrefix("piba-reg-pool-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
