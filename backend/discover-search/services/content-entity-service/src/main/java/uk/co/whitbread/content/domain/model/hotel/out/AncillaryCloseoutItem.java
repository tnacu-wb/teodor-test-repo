package uk.co.whitbread.content.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AncillaryCloseoutItem {

  private String text;
  private String startDate;
  private String endDate;
  private String serviceCode;
  private String upsellCodes;

}
