package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcuiExtraItemsDto {

  private BusinessItemsCcuiDto businessItems;
  private AccountCompanyItemsDto accountCompanyItems;
  private NonguaranteedItemsDto nonguaranteedItems;
  private Boolean sendMail;
  private Boolean cardPresent;
  private String addressCompanyName;
}
