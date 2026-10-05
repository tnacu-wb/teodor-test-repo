package uk.co.whitbread.hotel.account.client.cdh;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;

/**
 * Feigh client for accessing CDH endpoints.
 */
@FeignClient(value = "${feign.cdh.name:cdh}", url = "${feign.cdh.url}",
    fallbackFactory = CdhClientFallbackFactory.class)
public interface CdhClient {

  @PostMapping(value = "/v1/cdh/reservation/search",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  CdhReservationSearchDto getAccountBookingsV2(@Valid @RequestBody CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto);

}
