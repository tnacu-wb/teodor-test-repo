package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.NonguaranteedItemsDto;

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
