package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

public interface DistributionPersistenceAdapter {

  List<DistributionHotel> getHotelAvailabilitiesForDistribution(
      final DistributionPayload distributionPayload);
}
