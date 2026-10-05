package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StayGuestDto {

  private String title;
  private String firstName;
  private String lastName;
}
