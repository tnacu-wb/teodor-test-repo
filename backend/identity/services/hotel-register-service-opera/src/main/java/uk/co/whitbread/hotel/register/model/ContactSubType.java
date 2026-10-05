package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactSubType {
  mobile("Mobile"),
  landline("Landline");
  private final String type;
}
