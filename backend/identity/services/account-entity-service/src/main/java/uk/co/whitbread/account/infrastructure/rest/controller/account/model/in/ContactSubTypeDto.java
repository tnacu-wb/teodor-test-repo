package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactSubTypeDto {
  MOBILE("Mobile"),
  LANDLINE("Landline");
  private final String type;
}
