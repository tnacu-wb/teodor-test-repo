package uk.co.whitbread.account.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactSubType {
  MOBILE("Mobile"),
  LANDLINE("Landline");
  private final String type;
}
