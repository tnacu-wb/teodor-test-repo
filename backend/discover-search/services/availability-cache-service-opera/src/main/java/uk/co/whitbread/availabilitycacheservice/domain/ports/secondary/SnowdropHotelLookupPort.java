package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;

public interface SnowdropHotelLookupPort {

  List<HotelDetails> getHotelsFromLocation(final String placeId, final int radiusInMiles);

}
