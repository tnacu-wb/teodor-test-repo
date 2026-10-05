package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestToJoinAccepted {

  private final String email;
  private final String firstName;
  private final String lastName;
  private final String companyName;
  private final String language;
  private final String adminFirstName;
  private final String adminLastName;
  private final String bbActivatedVerificationUrl;
}
