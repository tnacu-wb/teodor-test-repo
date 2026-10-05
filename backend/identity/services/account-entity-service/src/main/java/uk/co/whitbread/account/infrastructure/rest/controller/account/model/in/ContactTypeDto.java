package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactTypeDto {
  EMAIL("Email"),
  PHONE("Telephone");
  private final String type;
}
