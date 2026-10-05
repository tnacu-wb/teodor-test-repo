package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestDto {

  private String title;
  private String firstName;
  private String lastName;
}
