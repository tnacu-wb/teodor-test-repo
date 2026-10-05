package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;

public interface GqtHotelAvailPostProcessorPort {

  List<GqtOperaHotelAvailabilities> processHotelResultSetToGqtHotelAvail(
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet,
      final GqtSearchPayload gqtSearchPayload);
}
