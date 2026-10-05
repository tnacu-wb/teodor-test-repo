package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.opera.out.HotelStatus;

public interface OnSaleFlagOutPort {

  List<HotelStatus> getHotelStatus(List<String> hotelId);

  HotelsMigrationStatusResponse getOnSaleFlag(
      HotelsMigrationStatusRequest hotelAvailabilitiesRequest);

}
