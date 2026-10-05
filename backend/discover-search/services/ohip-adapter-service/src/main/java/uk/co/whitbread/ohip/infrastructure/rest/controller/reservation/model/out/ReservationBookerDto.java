package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationBookerDto {

  private String profileId;
  private String email;
  private String title;
  private String firstName;
  private String lastName;
  private String mobile;
  private String landline;
  private ReservationBookerAddressDto address;
}
