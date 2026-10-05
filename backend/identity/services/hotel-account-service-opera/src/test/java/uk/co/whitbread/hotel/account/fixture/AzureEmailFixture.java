package uk.co.whitbread.hotel.account.fixture;

import uk.co.whitbread.shared.azureemail.model.AccountUpdate;

public class AzureEmailFixture {

  public static AccountUpdate createAccountUpdate(String email) {
    return AccountUpdate.builder()
        .email(email)
        .build();
  }
}
