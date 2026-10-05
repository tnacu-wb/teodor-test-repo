package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.DataValidationSearchRequest;

public interface DataValidationPort {

  List<HotelAvailabilitiesResultSet> getHotelAvailabilities(final DataValidationSearchRequest searchCriteria);

}
