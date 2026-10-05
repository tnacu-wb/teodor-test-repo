package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomTypeRequestDto {

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
  @Valid
  @Schema(required = true)
  private List<String> roomTypes;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String startDate;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String endDate;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String currency;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private List<Integer> adultsNumber;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private List<Integer> childrenNumber;
}
