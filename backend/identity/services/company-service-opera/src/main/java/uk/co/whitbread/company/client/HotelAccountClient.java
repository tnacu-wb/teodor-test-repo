package uk.co.whitbread.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.company.client.model.Customer;

@FeignClient(value = "${feign.hotelaccount.name}", url = "${feign.hotelaccount.url}", configuration = HotelAccountErrorDecodeConfig.class,
    fallbackFactory = HotelAccountClientFallbackFactory.class)
public interface HotelAccountClient {

  @GetMapping(value = "customers/hotels/{customer-id}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  Customer getCustomer(@PathVariable("customer-id") String customerId,
      @RequestHeader("session-id") String sessionId,
      @RequestParam("business") boolean business
  );
}
