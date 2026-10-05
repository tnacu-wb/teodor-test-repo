package uk.co.whitbread.domain.model.srp.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FacilityFilter {

  ACO("ACO"),
  HAC("HAC"),
  LFT("LFT"),
  HUL("HUL"),
  RES("RES"),
  DIN("DIN"),
  HRS("HRS"),
  HLG("HLG"),
  LUG("LUG"),
  WET("WET"),
  HAR("HAR");

  private final String value;
}
