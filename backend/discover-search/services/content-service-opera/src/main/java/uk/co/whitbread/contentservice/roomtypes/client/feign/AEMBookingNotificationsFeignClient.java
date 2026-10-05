package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsResponse;

@FeignClient(name = "aemBookingNotifications", url = "${feign-clients.aem.url}",
        configuration = FeignConfiguration.class,
        fallbackFactory = AEMFeignClientBookingNotificationsHystrixFallbackFactory.class)
public interface AEMBookingNotificationsFeignClient {
    @RequestMapping(value = "/{country}/{language}/business-booker/{resource}/{brand}/rate-{rate}.model.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AEMBookingNotificationsResponse getBookingNotifications(
            @PathVariable(value = "country") String country,
            @PathVariable(value = "language") String language,
            @PathVariable(value = "resource") String resource,
            @PathVariable(value = "brand") String brand,
            @PathVariable(value = "rate") String rate);
}