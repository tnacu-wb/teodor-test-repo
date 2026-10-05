package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;

public interface GqtHotelAvailabilitiesPort {

  List<GqtOperaHotelAvailabilities> getGqtHotelAvailabilities(final GqtSearchPayload gqtSearchPayload);

}
