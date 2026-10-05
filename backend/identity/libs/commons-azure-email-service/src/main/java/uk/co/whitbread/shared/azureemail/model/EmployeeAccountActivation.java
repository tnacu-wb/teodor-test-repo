package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeAccountActivation {

  private final String language;
  private final String companyName;
  private final String firstName;
  private final String lastName;
  private final String employeeEmail;
  private final String accessLevel;
  private final String activationLink;
}
