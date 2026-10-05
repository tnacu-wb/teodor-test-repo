package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CdhHeaders {
  ACCESSED_BY("AccessedBy"),
  ACCESS_CONTEXT("AccessContext");

  private final String header;
}
