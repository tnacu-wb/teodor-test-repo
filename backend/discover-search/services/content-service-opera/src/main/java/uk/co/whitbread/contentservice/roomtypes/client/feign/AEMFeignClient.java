package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomTypesResponse;

@FeignClient(name = "aem", url = "${feign-clients.aem.url}",
        configuration = FeignConfiguration.class,
        fallbackFactory = AEMFeignClientHystrixFallbackFactory.class)
public interface AEMFeignClient {
    @RequestMapping(value = "/{country}/{language}/{resource}/{brand}.model.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AEMRoomTypesResponse getRoomTypes(
            @PathVariable(value = "country") String country,
            @PathVariable(value = "language") String language,
            @PathVariable(value = "brand") String brand,
            @PathVariable(value = "resource") String resource);
}