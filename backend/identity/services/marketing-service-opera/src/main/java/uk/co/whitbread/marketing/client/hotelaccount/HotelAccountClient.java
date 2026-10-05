package uk.co.whitbread.marketing.client.hotelaccount;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.enums.BartHotelBrandCode;
import uk.co.whitbread.marketing.config.HotelAccountClientFallbackFactory;
import uk.co.whitbread.marketing.config.HotelAccountServiceErrorDecoder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(value = "${feign.hotelaccount.name}", url = "${feign.hotelaccount.url}",
        fallbackFactory = HotelAccountClientFallbackFactory.class, configuration = HotelAccountServiceErrorDecoder.class)
public interface HotelAccountClient {
    String DEFAULT_BRAND = "PI";
    String DEFAULT_PI_BOOKING_CHANNEL = "WEB";

    @GetMapping(value = "${hotelaccount.path}", produces = APPLICATION_JSON_VALUE)
    CustomerAccount getCustomer(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "hotel-brand", required = false, defaultValue = DEFAULT_BRAND) BartHotelBrandCode hotelBrand,
            @RequestHeader(value = "session-id") String sessionId,
            @RequestHeader(value = "Authorization") String jwt,
            @RequestHeader(required = false, defaultValue = DEFAULT_PI_BOOKING_CHANNEL) BartBookingChannelCode bookingChannel,
            @RequestParam(required = false) boolean business,
            @RequestHeader(name = "Origin", required = false) String origin);

}