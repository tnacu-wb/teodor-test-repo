package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationBookerSingleCall {
  private ReservationBookerAddress address;
  private String email;
  private String firstName;
  private String landline;
  private String lastName;
  private String mobile;
  private String title;
}