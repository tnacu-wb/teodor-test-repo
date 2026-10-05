package uk.co.whitbread.shared.azureemail.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeAccessLevelChange {

  private final String language;
  private final String firstName;
  private final String email;
  private final String accessLevel;
  private final String loginUrl;
}
