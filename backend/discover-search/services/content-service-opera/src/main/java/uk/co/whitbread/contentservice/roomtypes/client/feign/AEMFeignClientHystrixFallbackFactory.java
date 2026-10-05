package uk.co.whitbread.contentservice.roomtypes.client.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomTypesResponse;

@Slf4j
@Component
public class AEMFeignClientHystrixFallbackFactory implements FallbackFactory<AEMFeignClient> {

    @Override
    public AEMFeignClient create(Throwable throwable) {

        return (country, language, brand, resource) -> {

            log.error("Failed to call aem to get locations (or circuit breaker is open) " +
                    "country={}, language={}, brand={}, resource={}", country, language, brand, resource, throwable);
            return AEMRoomTypesResponse
                    .builder()
                    .build();
        };
    }
}