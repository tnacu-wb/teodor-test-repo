package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AncillaryCloseoutItemDto {

  private String text;
  private String startDate;
  private String endDate;
  private String serviceCode;
  private String upsellCodes;

}
