package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

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
public class RoomRateDto {

  @Schema(example = "FLEXRATE", required = true)
  private String ratePlanCode;
  @Schema(example = "PBF", required = true)
  private String rateDisplaySet;
  @Schema(example = "EMP01")
  private String cellCode;
  private boolean twinRoomTypeAvailability;
  private List<RoomTypeDto> roomTypes;
  private String globalCompanyId;
  private String promotionCode;

}
