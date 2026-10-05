package uk.co.whitbread.marketing.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.enums.BartHotelBrandCode;
import uk.co.whitbread.marketing.client.hotelaccount.CustomerAccount;
import uk.co.whitbread.marketing.client.hotelaccount.HotelAccountClient;
import uk.co.whitbread.marketing.exception.HotelAccountClientException;

@Slf4j
@Component
public class HotelAccountClientFallbackFactory implements FallbackFactory<HotelAccountClient> {

    @Override
    public HotelAccountClient create(Throwable throwable) {
        return (String customerId,
                BartHotelBrandCode hotelBrand,
                String sessionId,
                String jwt,
                BartBookingChannelCode bookingChannel,
                boolean business,
                String origin) -> {
            if (throwable instanceof HttpStatusCodeException) {
                HttpStatusCodeException e = (HttpStatusCodeException) throwable;
                log.error("Failed to call Hotel account: customerId {}, hotelBrand {}, sessionId {}, bookingChannel {}," +
                        "business {}, origin {}, body {}", customerId, hotelBrand, sessionId, bookingChannel, business, origin, e.getResponseBodyAsString());
                throw e;
            } else if (throwable instanceof HotelAccountClientException) {
                throw (HotelAccountClientException) throwable;
            }
            log.error("Circuit breaker is probably tripped {}", throwable.getMessage(), throwable);
            return new CustomerAccount();
        };

    }
}

