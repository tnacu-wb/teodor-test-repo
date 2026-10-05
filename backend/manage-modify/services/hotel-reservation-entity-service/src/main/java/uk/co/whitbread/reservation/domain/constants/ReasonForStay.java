package uk.co.whitbread.reservation.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReasonForStay {

  LEI("NTLEI"),
  BUS("NTBUS"),
  OTH("NTLEI");

  private final String code;

}
