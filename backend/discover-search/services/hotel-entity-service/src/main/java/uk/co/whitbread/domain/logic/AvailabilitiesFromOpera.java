package uk.co.whitbread.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.shared.auth.account.Account;

@RequiredArgsConstructor
@Slf4j
public class AvailabilitiesFromOpera {
  private final HotelAvailabilityOutPort ohipAdapterOutPort;

  public HotelAvailabilitiesResponse getOperaHotelsAvailabilities(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      Account account,
      List<HotelMigrationStatusResponse> operaHotels) {
    if (account != null) {
      hotelAvailabilitiesRequest = hotelAvailabilitiesRequest.toBuilder().companyId(account.getBartId())
          .build();
    }

    if (hotelAvailabilitiesRequest.getSort() != null
        && AvailabilitySortOption.RECOMMENDATION.name().equals(hotelAvailabilitiesRequest.getSort())) {
      hotelAvailabilitiesRequest = hotelAvailabilitiesRequest.toBuilder()
          .sort(AvailabilitySortOption.DISTANCE.name())
          .build();
    }

    return ohipAdapterOutPort
            .getMultiHotelAvailabilityWithNewOperaEndpoint(hotelAvailabilitiesRequest, operaHotels);
  }
}
