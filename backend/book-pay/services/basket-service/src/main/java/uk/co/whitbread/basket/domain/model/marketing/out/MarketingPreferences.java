package uk.co.whitbread.basket.domain.model.marketing.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingPreferences {

  private Boolean optIn;
  private Customer customer;
  private String contactValue;

}
