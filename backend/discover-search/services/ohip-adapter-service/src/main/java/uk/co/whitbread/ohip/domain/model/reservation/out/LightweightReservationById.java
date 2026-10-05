package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LightweightReservationById {
  private String reservationId;
  private String hotelId;
  private String checkInTime;
  private String checkOutTime;
  private String email;
  private String purposeOfStay;
  private List<ReservationPackagesDetailsResponse> reservationPackageList;
}
