package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.DataValidationSearchRequest;

public interface DataValidationPersistenceAdapter {

  List<HotelAvailabilitiesResultSet> getHotelAvailabilitiesForDataValidation(
      final DataValidationSearchRequest searchCriteria);

}
