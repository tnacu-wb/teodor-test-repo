package uk.co.whitbread.hotel.register.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.hotel.register.model.CountriesResponse;

@FeignClient(value = "${feign.hotelcountries.name}", url = "${feign.hotelcountries.url}")
public interface CountriesClient {

    @GetMapping("/countries")
    CountriesResponse getCountries(@RequestHeader("country") String country,
        @RequestHeader("language") String language);
}
