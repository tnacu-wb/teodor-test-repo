package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice;

import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.LocationPriceRequest;

public interface LocationPricePersistencePort {


  void updateBestHotelPricesForLocations(LocalDate availDate, String hotelCode);

  List<Hotel> findBestPricePerLocation(LocationPriceRequest locationPriceRequest);

}
