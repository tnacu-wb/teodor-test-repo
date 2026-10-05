package uk.co.whitbread.shared.azureemail.model;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeRegistrationNotification {

  private final String language;
  private final String companyName;
  private final String emailAddress;
  private final String firstName;
  private final String lastName;
  private final String requesterFirstName;
  private final String requesterLastName;
}
