package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public abstract class UpdateRequestDto {

  @NotEmpty
  @Valid
  @JsonProperty("basketReferenceId")
  @Schema(required = true)
  private String basketReferenceId;

  @Schema(example = "LONSTM")
  private String hotelId;

  @Schema(example = "EUR")
  private String currency;

  @Schema(example = "DOUBLE,FMQUAD")
  @JsonProperty("roomType")
  private List<String> roomTypes;

  @NotEmpty
  @Valid
  @JsonProperty("startDate")
  @Schema(example = "2015-10-20", required = true)
  private String startDate;

  @NotEmpty
  @Valid
  @JsonProperty("endDate")
  @Schema(example = "2015-10-21", required = true)
  private String endDate;

  @NotEmpty
  @Valid
  @JsonProperty("rateCode")
  @Schema(example = "FLEXRATE")
  private String rateCode;

  @NotEmpty
  @JsonProperty("adultsNumber")
  private List<Integer> adultsNumber;

  @JsonProperty("childrenNumber")
  private List<Integer> childrenNumber;

}
