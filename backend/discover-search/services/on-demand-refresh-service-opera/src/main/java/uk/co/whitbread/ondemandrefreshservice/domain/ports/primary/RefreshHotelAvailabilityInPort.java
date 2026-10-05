package uk.co.whitbread.ondemandrefreshservice.domain.ports.primary;

import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;

import java.util.Set;

public interface RefreshHotelAvailabilityInPort {

    OnDemandProcessResponse refreshOnDemandWithDates(Set<String> hotelIds, String startDate, String endDate);

    void refreshOnDemandWithoutDates();
}
