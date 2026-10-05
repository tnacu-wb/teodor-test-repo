package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
