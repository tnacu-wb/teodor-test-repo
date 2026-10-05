package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookerDetailsCnp {

  private String title;
  private String firstName;
  private String lastName;
  private String mobile;
  private String landline;
  private String emailAddress;
  private String companyName;
  private BookerAddressCnp address;
}
