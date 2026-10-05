package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.AemLocationResponse;

@Slf4j
@Component
public class AemFeignClientHystrixFallbackFactory implements FallbackFactory<AemFeignClient> {

  @Override
  public AemFeignClient create(Throwable throwable) {

    return (country, language, path) -> {

      log.error("Failed to call aem to get locations (or circuit breaker is open) "
          + "country={}, language={}, path={}", country, language, path, throwable);
      return AemLocationResponse
          .builder()
          .build();
    };
  }
}
