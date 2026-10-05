package uk.co.whitbread.dashboard.domain.ports.secondary;

import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;

public interface CdhReservationsOutPort {

  CdhReservationSearchDto retrieveBooking(String reservationId);

}
