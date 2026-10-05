package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class RoomRateDto {

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private String start;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  private String end;
  @NotEmpty
  @Schema(example = "SDB", required = true)
  private String roomType;
  @NotEmpty
  @Schema(example = "DAILY", required = true)
  private String ratePlanCode;
  @Schema(example = "SNGL")
  private List<String> specialRequests;
  @Schema(example = "ABC")
  private String cellCode;
  private List<RatePriceDto> ratePrices;
  private String promotionCode;
}
