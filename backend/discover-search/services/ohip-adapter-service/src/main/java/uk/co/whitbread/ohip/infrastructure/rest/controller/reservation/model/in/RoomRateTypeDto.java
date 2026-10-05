package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import uk.co.whitbread.ohip.infrastructure.rest.controller.validation.DateFormat;

@Data
public class RoomRateTypeDto {

  @NotNull
  @Valid
  private RateTypeDto rates;

  @NotEmpty
  @Valid
  private String roomType;

  @NotEmpty
  @Valid
  private String ratePlanCode;

  @NotEmpty
  @DateFormat
  @Valid
  private String start;

  @NotEmpty
  @DateFormat
  @Valid
  private String end;

  @NotEmpty
  @Valid
  private String sourceCode;

  @Positive
  @Valid
  private Integer numberOfUnits;

}
