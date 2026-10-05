package uk.co.whitbread.hotel.card.client.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.hotel.card.client.account.model.Customer;

@FeignClient(value = "${feign.hotelaccount.name}", url = "${feign.hotelaccount.url}", configuration = HotelAccountErrorDecodeConfig.class,
    fallbackFactory = HotelAccountClientFallbackFactory.class)
public interface HotelAccountClient {

    @RequestMapping(value = "customers/hotels/{customer-id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    Customer getCustomer(@PathVariable("customer-id") String customerId,
                         @RequestHeader("session-id") String sessionId,
                         @RequestParam("business") boolean business
    );
}
