package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationBooker {

  private ReservationBookerAddress address;
  private String email;
  private String firstName;
  private String landline;
  private String lastName;
  private String mobile;
  private String title;
}
