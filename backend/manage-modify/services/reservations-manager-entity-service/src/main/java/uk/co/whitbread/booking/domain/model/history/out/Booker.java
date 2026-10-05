package uk.co.whitbread.booking.domain.model.history.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booker {
  private String companyName;
  private String emailAddress;
  private String firstName;
  private String lastName;
  private String mobile;
  private String telephoneNumber;
}
