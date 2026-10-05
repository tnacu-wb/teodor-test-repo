package uk.co.whitbread.domain.model.srp.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ParkingFilter {

  FREE_PARKING("CPF"),
  CHARGEABLE_ON_SITE_PARKING("CPP"),
  CHARGEABLE_OFF_SITE_PARKING("COP"),
  CHARGEABLE_OFF_SITE_PARKING_ALT("COC");

  private final String value;
}
