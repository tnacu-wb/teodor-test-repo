package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem.AemFeignConfiguration;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.AemLocationResponse;

@FeignClient(name = "aem", url = "${feign-clients.aem.url}",
    configuration = AemFeignConfiguration.class,
    fallbackFactory = AemFeignClientHystrixFallbackFactory.class)
public interface AemFeignClient {

  @GetMapping(value = "/{country}/{language}/{path}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AemLocationResponse getLocations(
      @PathVariable(value = "country") String country,
      @PathVariable(value = "language") String language,
      @PathVariable(value = "path") String path);
}

