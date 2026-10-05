package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

public interface DistributionHotelAvailabilitiesPort {

  List<DistributionHotel> getHotelAvailabilities(final DistributionPayload distributionPayload);

}
