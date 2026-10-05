package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationBooker {

  private String email;
  private String profileId;
  private String title;
  private String firstName;
  private String lastName;
  private String mobile;
  private String landline;
  private ReservationBookerAddress address;
}
