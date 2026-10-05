package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

public interface DistributionHotelsPostProcessorPort {

  List<DistributionHotel> performDistributionHotelsPostProcess(DistributionPayload distributionPayload,
      List<DistributionHotelAvailResultWithRestrictionSet> hotelAvailabilitiesResultSet);

}
