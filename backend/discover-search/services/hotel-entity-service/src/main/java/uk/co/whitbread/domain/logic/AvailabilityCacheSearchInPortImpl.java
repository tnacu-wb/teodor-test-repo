package uk.co.whitbread.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.domain.model.availabilitycache.in.AvailabilityCacheSearchCriteria;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.ports.primary.AvailabilityCacheSearchInPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheSearchOutPort;

@RequiredArgsConstructor
public class AvailabilityCacheSearchInPortImpl implements AvailabilityCacheSearchInPort {

  private final AvailabilityCacheSearchOutPort availabilityCacheOutPort;

  @Override
  public HotelAvailabilitiesResponse getAvailabilitiesFromAvailabilityCache(
      AvailabilityCacheSearchCriteria searchCriteria) {
    return availabilityCacheOutPort.getAvailabilitiesFromAvailabilityCache(searchCriteria);
  }

}
