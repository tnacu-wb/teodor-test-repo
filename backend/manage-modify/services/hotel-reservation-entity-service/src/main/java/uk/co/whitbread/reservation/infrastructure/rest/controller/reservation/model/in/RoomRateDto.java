package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomRateDto {

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  @FutureOrPresent
  private LocalDate startDate;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  @FutureOrPresent
  private LocalDate endDate;
  @NotEmpty
  @Schema(example = "FMTRPL", required = true)
  private String pmsRoomType;
  @NotEmpty
  @Schema(example = "FLEXRATE", required = true)
  private String ratePlanCode;
  @Schema(example = "NEG")
  private String rateDisplaySet;
  @Schema(example = "SNGL")
  private List<String> specialRequests;
  @Schema(example = "ABC")
  private String cellCode;
  private List<RatePriceDto> ratePrices;
  private String promotionCode;
  private PromoKind promoKind;
}
