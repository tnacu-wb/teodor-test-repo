package uk.co.whitbread.hotel.register.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CompanyType {

  BB("BB"),
  INNB("INNB"),
  BP("BP");

  private final String value;
}
