package uk.co.whitbread.availabilitycacheservice.domain.ports.primary.hotelprice;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

public interface LocationPricePort {

  List<Hotel> getBestPricedHotels(final LocationPriceRequest locationPriceRequest);
}
