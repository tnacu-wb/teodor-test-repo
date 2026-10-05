package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDetailsDto {

  private String givenName;
  private String surname;
  private String nameTitle;
  private String nameType;
  private String nationality;
  private KioskAddressDto kioskAddress;
  private String emailAddress;
  private String phoneNumber;
  private PassportDetailsDto passportDetails;


}
