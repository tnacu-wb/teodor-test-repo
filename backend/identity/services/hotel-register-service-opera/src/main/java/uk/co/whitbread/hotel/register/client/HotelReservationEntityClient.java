package uk.co.whitbread.hotel.register.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.hotel.register.config.FeignConfig;
import uk.co.whitbread.hotel.register.model.ReservationRequest;

@FeignClient(value = "${feign.hotel-reservation-entity-service-host.name}", url = "${feign.hotel-reservation-entity-service-host.url}", configuration = FeignConfig.class)
public interface HotelReservationEntityClient {

    @PutMapping("/v1/reservations/link-leisure-customer")
    void linkLeisureCustomer(@RequestBody ReservationRequest request);
}
