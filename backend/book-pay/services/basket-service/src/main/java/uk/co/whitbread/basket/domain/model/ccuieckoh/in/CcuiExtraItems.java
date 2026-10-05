package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcuiExtraItems {

  private BusinessItems businessItems;
  private AccountCompanyItems accountCompanyItems;
  private NonguaranteedItems nonguaranteedItems;
  private Boolean cardPresent;
  private String addressCompanyName;
}
