package uk.co.whitbread.infrastructure.rest.client.availabilitycache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheSearchOutPort;
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheClient;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper.AvailabilityCacheResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper.AvailabilityCacheSearchMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class AvailabilityCacheSearchOutPortImpl implements AvailabilityCacheSearchOutPort {

  private final AvailabilityCacheSearchMapper availabilityCacheSearchMapper;
  private final AvailabilityCacheResponseMapper availabilityCacheResponseMapper;
  private final AvailabilityCacheClient availabilityCacheClient;

  public HotelAvailabilitiesResponse getAvailabilitiesFromAvailabilityCache(
      AvailabilityCacheSearchCriteria searchCriteria) {
    log.debug(
        "Entered getAvailabilitiesFromAvailabilityCache with searchCriteria={}",
        searchCriteria);

    var availabilityCacheRequest = availabilityCacheSearchMapper.toDto(searchCriteria);
    var availabilityCacheResponse =
        availabilityCacheClient.getAvailabilitiesFromCache(availabilityCacheRequest);
    return availabilityCacheResponseMapper.toModel(availabilityCacheResponse);
  }
}
