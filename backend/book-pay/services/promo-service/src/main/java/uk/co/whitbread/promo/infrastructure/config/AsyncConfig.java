package uk.co.whitbread.promo.infrastructure.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

  @Bean(name = "promoCopyExecutor")
  public ThreadPoolTaskExecutor promoCopyExecutor(
          @Value("${app.async.core:1}") int core,
          @Value("${app.async.max:1}") int max,
          @Value("${app.async.queue:500}") int queue,
          @Value("${app.async.await-termination-seconds:120}") int awaitSeconds) {

    return buildExecutor(core, max, queue, awaitSeconds, "promo-copy-");
  }

  @Bean(name = "s3Executor")
  public ThreadPoolTaskExecutor s3Executor(
          @Value("${app.async.s3.core:1}") int core,
          @Value("${app.async.s3.max:1}") int max,
          @Value("${app.async.s3.queue:50}") int queue,
          @Value("${app.async.s3.await-termination-seconds:120}") int awaitSeconds) {

    return buildExecutor(core, max, queue, awaitSeconds, "s3-upload-");
  }

  private ThreadPoolTaskExecutor buildExecutor(
          int core,
          int max,
          int queue,
          int awaitSeconds,
          String threadPrefix) {

    ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
    exec.setCorePoolSize(core);
    exec.setMaxPoolSize(max);
    exec.setQueueCapacity(queue);
    exec.setThreadNamePrefix(threadPrefix);
    exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    exec.setTaskDecorator(new MdcTaskDecorator());
    exec.setWaitForTasksToCompleteOnShutdown(true);
    exec.setAwaitTerminationSeconds(awaitSeconds);
    exec.initialize();

    return exec;
  }

  static class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> parentContext = MDC.getCopyOfContextMap();
      return () -> {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();

        try {
          if (parentContext != null) {
            MDC.setContextMap(parentContext);
          } else {
            MDC.clear();
          }

          log.debug("MDC context transferred to async thread: {}",
                  parentContext != null ? parentContext.keySet() : "empty");

          runnable.run();
        } finally {
          if (previousContext != null) {
            MDC.setContextMap(previousContext);
          } else {
            MDC.clear();
          }
          log.debug("MDC context restored after async task completion");
        }
      };
    }
  }
}
