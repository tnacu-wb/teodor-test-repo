package uk.co.whitbread.reservation.domain.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelReservationCache {

  private String hotelId;
  private String startDate;
  private String endDate;
  private int nightsNumber;
  private String rateCode;
  private int adultsNumber;
  private int childrenNumber;
  private String reservationId;
  private String bookingFlowId;
}
