package uk.co.whitbread.shared.cdh.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CdhHeaders {
  ACCESSED_BY("AccessedBy"),
  ACCESS_CONTEXT("AccessContext");

  private final String header;
}
