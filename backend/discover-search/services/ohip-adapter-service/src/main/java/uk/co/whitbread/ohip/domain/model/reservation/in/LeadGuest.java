package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.ohip.domain.model.checkin.out.Address;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class LeadGuest {

  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String language;
  private Address address;

}
