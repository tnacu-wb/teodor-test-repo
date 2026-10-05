package uk.co.whitbread.account.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SourceDetails {
  private String channel;
  private String journey;
  private String locale;
}
