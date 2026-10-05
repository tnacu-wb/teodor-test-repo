package uk.co.whitbread.contentservice.roomtypes.client.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsResponse;

@Slf4j
@Component
public class AEMFeignClientBookingNotificationsHystrixFallbackFactory implements
    FallbackFactory<AEMBookingNotificationsFeignClient> {

    @Override
    public AEMBookingNotificationsFeignClient create(Throwable throwable) {

        return (country, language, resource, brand, rate) -> {
            log.error("Failed to call aem to get booking notifications for Business Booker " +
                    "country={}, language={}, resource={}, brand={}, rate={}", country, language, resource, brand, rate, throwable);
            return AEMBookingNotificationsResponse
                    .builder()
                    .build();
        };
    }
}
