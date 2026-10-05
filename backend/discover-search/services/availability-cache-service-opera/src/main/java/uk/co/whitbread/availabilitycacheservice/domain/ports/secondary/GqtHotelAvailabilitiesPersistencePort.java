package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;


public interface GqtHotelAvailabilitiesPersistencePort {

  List<GqtOperaHotelAvailabilities> getGqtHotelAvailabilitiesForOpera(final GqtSearchPayload gqtSearchPayload);
}
