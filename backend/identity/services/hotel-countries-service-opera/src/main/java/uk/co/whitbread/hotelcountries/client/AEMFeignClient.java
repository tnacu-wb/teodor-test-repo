package uk.co.whitbread.hotelcountries.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.hotelcountries.config.AEMFeignConfiguration;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;

@FeignClient(name = "${feign-clients.aem.name:aem}", url = "${feign-clients.aem.url}",
        configuration = AEMFeignConfiguration.class)
public interface AEMFeignClient {
    @RequestMapping(value = "/{country}/{language}/{path}",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_XML_VALUE)
    AemCountries getAemCountries(
            @PathVariable(value = "country") String country,
            @PathVariable(value = "language") String language,
            @PathVariable(value = "path") String path);
}