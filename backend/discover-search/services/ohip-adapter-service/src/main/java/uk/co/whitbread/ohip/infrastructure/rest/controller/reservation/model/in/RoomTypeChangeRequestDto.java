package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class RoomTypeChangeRequestDto {

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String basketReferenceId;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private List<String> reservationIds;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String hotelId;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String rateCode;

  @NotEmpty
  @Size(min = 1)
  @Valid
  private List<String> roomTypes;

  @NotEmpty
  @Valid
  @Schema(required = true, format = "2022-12-01")
  private String startDate;

  @NotEmpty
  @Valid
  @Schema(required = true, format = "2022-12-03")
  private String endDate;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String currency;

  @NotEmpty
  @Size(min = 1)
  private List<Integer> adultsNumber;

  private List<Integer> childrenNumber;
}
