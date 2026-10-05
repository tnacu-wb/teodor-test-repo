package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class PiRegister {
  
  private final String address1;
  private final String address2;
  private final String address3;
  private final String country;
  private final String emailAddress;
  private final String guestForename;
  private final String guestSurname;
  private final String guestTitle;
  private final String infName;
  private final String languageCode;
  private final String postCode;
  private final String telephone;
  private final String userName;
}
