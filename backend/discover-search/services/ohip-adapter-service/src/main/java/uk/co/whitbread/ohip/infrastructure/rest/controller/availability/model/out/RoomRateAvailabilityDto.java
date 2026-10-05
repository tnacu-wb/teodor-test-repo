package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateAvailabilityDto {

  @Schema(example = "FLEXRATE", required = true)
  private String ratePlanCode;
  @Schema(example = "PROMO")
  private String promotionCode;
  private List<RoomTypeDto> roomTypes;

}