package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;


@FeignClient(value = "${feign.content-service.name}", url = "${feign.content-service.url}",
    fallbackFactory = ContentClientHystrixFallbackFactory.class)
public interface ContentClient {

  @GetMapping(value = "/content/rateclassifications", produces = MediaType.APPLICATION_JSON_VALUE)
  RateClassificationsList getRateClassifications(
      @RequestParam(value = "brand", defaultValue = "pi", required = false) String brand,
      @RequestParam(value = "language", defaultValue = "en", required = false) String language,
      @RequestParam(value = "hotelCode", required = false) String hotelCode);
}
