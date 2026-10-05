package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;

@Slf4j
@Component
public class ContentClientHystrixFallbackFactory implements FallbackFactory<ContentClient> {

  @Override
  public ContentClient create(Throwable throwable) {
    return (brand, language, hotelCode) -> {
      log.trace("input parameters are:: brand {}, language {}, hotelCode {}", brand, language, hotelCode);
      List<RateClassification> emptyList = Collections.emptyList();
      RateClassificationsList fallback = RateClassificationsList.builder().rateClassifications(emptyList).build();
      log.error(
          "Failed to call content-service (or circuit breaker is open) when retrieving list of "
              + "RateClassifications for brand={}, language={}, hotelCode={}. Returning fallback "
              + "RateClassifications={}",
          brand, language, hotelCode, fallback, throwable);
      return fallback;
    };
  }
}
