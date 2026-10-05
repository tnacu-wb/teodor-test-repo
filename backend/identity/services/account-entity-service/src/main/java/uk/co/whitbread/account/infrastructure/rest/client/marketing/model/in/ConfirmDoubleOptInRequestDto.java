package uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactSubTypeDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactTypeDto;

@Data
@AllArgsConstructor
public class ConfirmDoubleOptInRequestDto {
  private String[] brandCodes;
  private String customerId;
  private ContactTypeDto contactType;
  private ContactSubTypeDto contactSubType;
}
