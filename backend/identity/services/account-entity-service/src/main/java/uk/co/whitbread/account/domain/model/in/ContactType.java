package uk.co.whitbread.account.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactType {
  EMAIL("Email"),
  PHONE("Telephone");
  private final String type;
}
