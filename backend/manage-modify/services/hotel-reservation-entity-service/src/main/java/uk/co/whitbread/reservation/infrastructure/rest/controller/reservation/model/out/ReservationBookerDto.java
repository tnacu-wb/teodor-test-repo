package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationBookerDto {

  private String email;
  private String title;
  private String firstName;
  private String lastName;
  private String mobile;
  private String landline;
  private ReservationBookerAddressDto address;
}
