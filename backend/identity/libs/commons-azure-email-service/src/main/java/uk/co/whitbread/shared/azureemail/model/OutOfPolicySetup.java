package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OutOfPolicySetup {

  private final String email;
  private final String firstName;
  private final String lastName;
}
