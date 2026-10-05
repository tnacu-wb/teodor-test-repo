package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordReset {

  private final String resetPasswordUrl;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final String language;
}
