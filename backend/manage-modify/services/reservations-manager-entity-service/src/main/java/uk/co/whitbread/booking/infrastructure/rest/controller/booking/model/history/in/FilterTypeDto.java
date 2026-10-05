package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FilterTypeDto {

  NAME,
  ARRIVAL_DATE,
  CONFIRM_NUMBER;

  @JsonCreator
  public static FilterTypeDto fromString(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    return FilterTypeDto.valueOf(value);
  }
}
