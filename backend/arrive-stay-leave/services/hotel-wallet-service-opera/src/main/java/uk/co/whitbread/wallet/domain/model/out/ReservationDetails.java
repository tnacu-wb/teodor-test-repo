package uk.co.whitbread.wallet.domain.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetails {
  private String title;
  private String firstName;
  private String lastName;
  private String confirmationNumber; //  referenceNo?
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String checkInTime;
  private String checkOutTime;
  private String contactCentre;
  private String hotelId;
}
