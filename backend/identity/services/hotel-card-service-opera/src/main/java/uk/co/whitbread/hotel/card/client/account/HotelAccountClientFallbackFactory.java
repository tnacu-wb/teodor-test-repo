package uk.co.whitbread.hotel.card.client.account;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HotelAccountClientFallbackFactory implements FallbackFactory<HotelAccountClient> {

    @Override
    public HotelAccountClient create(Throwable throwable) {
        return (customerId, sessionId, business) -> {
            if (throwable instanceof FeignException feignException && feignException.status() < 500) {
                throw feignException;
            }

            log.warn("Failed to call 'hotel-account' when retrieving Customer for " +
                            "customerId={} sessionId={} business={}, returning fallback.",
                    customerId, sessionId, business, throwable);

            return null;
        };
    }
}
