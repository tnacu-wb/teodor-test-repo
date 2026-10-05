package uk.co.whitbread.employee.bulk.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.employee.bulk.model.CountriesResponse;

@FeignClient(value = "${feign.hotelcountries.name:hotelcountries}", url = "${feign.hotelcountries.url:url}")
public interface CountriesClient {

    @GetMapping("/countries")
    CountriesResponse getCountries(@RequestHeader("country") String country, @RequestHeader("language") String language);
}
