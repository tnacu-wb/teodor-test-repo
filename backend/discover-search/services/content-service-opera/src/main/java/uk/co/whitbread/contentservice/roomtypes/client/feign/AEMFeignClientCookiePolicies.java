package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePoliciesResponse;

@FeignClient(name = "aemCookiePolicies", url = "${feign-clients.aem.url}",
        configuration = FeignConfiguration.class, fallbackFactory = AEMFeignCookiePolicyHystrixFallbackFactory.class)
public interface AEMFeignClientCookiePolicies {
    @RequestMapping(value = "/{country}/{language}/{resource}/{brand}.model.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AEMCookiePoliciesResponse getCookiePolicies(
            @PathVariable(value = "country") String country,
            @PathVariable(value = "language") String language,
            @PathVariable(value = "brand") String brand,
            @PathVariable(value = "resource") String resource);
    @RequestMapping(value = "/{country}/{language}/{resource}/{brand}/{subBrand}.model.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AEMCookiePoliciesResponse getCookiePolicies(
            @PathVariable(value = "country") String country,
            @PathVariable(value = "language") String language,
            @PathVariable(value = "brand") String brand,
            @PathVariable(value = "subBrand") String subBrand,
            @PathVariable(value = "resource") String resource);
}
