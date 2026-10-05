package uk.co.whitbread.ohip.infrastructure.config;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestrictionsExecutorServiceConfig {
  private final ExecutorService restrictionsExecutorService;

  public RestrictionsExecutorServiceConfig(
      @Value("${config.service.ohip.restrictions.parallelThreads:5}") int restrictionsParallelThreads) {
    this.restrictionsExecutorService = Executors.newFixedThreadPool(restrictionsParallelThreads);
  }

  @Bean(name = "restrictionsExecutorService")
  public ExecutorService restrictionsExecutorService() {
    return restrictionsExecutorService;
  }

  @PreDestroy
  public void shutdownExecutor() {
    if (restrictionsExecutorService == null || restrictionsExecutorService.isShutdown()) {
      return;
    }
    restrictionsExecutorService.shutdown();
    try {
      if (!restrictionsExecutorService.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
        restrictionsExecutorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      restrictionsExecutorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

}
