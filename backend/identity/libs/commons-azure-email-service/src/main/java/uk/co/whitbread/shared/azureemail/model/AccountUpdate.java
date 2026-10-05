package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountUpdate {

  private final String title;
  private final String firstName;
  private final String lastName;
  private final String addressLine1;
  private final String addressLine2;
  private final String addressLine3;
  private final String countryCode;
  private final String postCode;
  private final String telephone;
  private final String mobile;
  private final String email;
}
