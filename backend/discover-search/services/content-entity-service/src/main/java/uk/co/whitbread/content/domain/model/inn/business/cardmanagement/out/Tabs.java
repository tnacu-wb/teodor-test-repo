package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tabs {

  private CentrallyStored centrallyStored;
  private String centrallyStoredTitle;
  private String innBusinessPay;

}
