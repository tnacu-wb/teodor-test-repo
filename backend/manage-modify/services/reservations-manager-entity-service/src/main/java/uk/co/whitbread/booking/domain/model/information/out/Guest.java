package uk.co.whitbread.booking.domain.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Guest {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
}
