package uk.co.whitbread.ohip.domain.model.profile.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDetails {

  private String givenName;
  private String surname;
  private String nameTitle;
  private String nameType;
  private String nationality;
  private KioskAddress kioskAddress;
  private String emailAddress;
  private String phoneNumber;
  private PassportDetails passportDetails;

}
