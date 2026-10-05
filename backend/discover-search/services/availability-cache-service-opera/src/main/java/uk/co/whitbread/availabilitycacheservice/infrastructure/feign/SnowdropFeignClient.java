package uk.co.whitbread.availabilitycacheservice.infrastructure.feign;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.snowdrop.SnowdropConfiguration;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.SnowdropLookupException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.snowdrop.HotelDetailsDto;

@FeignClient(name = "snowdrop", url = "${feign-clients.snowdrop.host}",
    configuration = SnowdropConfiguration.class,
    fallbackFactory = SnowdropFeignClient.SnowdropFeignClientFallbackFactory.class)
public interface SnowdropFeignClient {

  @GetMapping(value = "/v1/search/hotels", produces = MediaType.APPLICATION_JSON_VALUE)
  List<HotelDetailsDto> getHotelsFromSnowdrop(@RequestParam(value = "placeId") String placeId,
      @RequestParam(value = "radius") String radius);

  @Slf4j
  @Component
  class SnowdropFeignClientFallbackFactory implements FallbackFactory<SnowdropFeignClient> {

    @Override
    public SnowdropFeignClient create(Throwable throwable) {
      return (placeId, radius) -> {
        String errorMsg = "Failed to call snowdrop to get hotels by placeId=" + placeId
            + " and radius=" + radius;
        log.error(errorMsg, throwable);
        throw new SnowdropLookupException(errorMsg, throwable);
      };
    }
  }
}