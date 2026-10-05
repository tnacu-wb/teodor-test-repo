package uk.co.whitbread.company.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HotelAccountClientFallbackFactory implements FallbackFactory<HotelAccountClient> {

    @Override
    public HotelAccountClient create(Throwable throwable) {
        return (customerId, sessionId, business) -> {
            log.warn("Failed to call 'hotel-account' when retrieving Customer for " +
                            "customerId={} sessionId={} business={}, returning fallback.",
                    customerId, sessionId, business, throwable);
            return null;
        };
    }
}
