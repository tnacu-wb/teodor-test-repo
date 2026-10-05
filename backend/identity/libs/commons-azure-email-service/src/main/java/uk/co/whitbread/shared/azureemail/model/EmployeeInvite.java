package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeInvite {

  private final String email;
  private final String firstName;
  private final String lastName;
  private final String accessLevel;
  private final String activationLink;
  private final String language;
}
