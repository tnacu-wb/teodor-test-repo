package uk.co.whitbread.promo.infrastructure.rest.client.ohip.model.out;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class PromotionResponseDto {

  private String promotionName;
  private LocalDate bookingStartDate;
  private LocalDate bookingEndDate;
  private LocalDate stayStartDate;
  private LocalDate stayEndDate;
  private String promotionCode;
}
