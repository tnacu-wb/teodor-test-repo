package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class UpdateReasonForStayRequestDto {

  @NotNull
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", required = true)
  private String reasonForStay;
  @NotNull
  private List<String> reservationIds;

}