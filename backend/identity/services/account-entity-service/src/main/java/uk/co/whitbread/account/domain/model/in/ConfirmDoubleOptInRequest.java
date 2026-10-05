package uk.co.whitbread.account.domain.model.in;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ConfirmDoubleOptInRequest {
  private String[] brandCodes;
  private String customerId;
  private ContactType contactType;
  private ContactSubType contactSubType;
}
