package uk.co.whitbread.contentservice.roomtypes.client.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassificationsResponse;

@Slf4j
@Component
public class AEMFeignClientRatesHystrixFallbackFactory implements
    FallbackFactory<AEMFeignClientRates> {

    @Override
    public AEMFeignClientRates create(Throwable throwable) {

        return (country, language, brand, resource) -> {

            log.error("Failed to call aem to get rate locations (or circuit breaker is open) " +
                    "country={}, language={}, brand={}, path={}", country, language, brand, resource, throwable);
            return AEMRateClassificationsResponse
                    .builder()
                    .build();
        };
    }
}
