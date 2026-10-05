package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReasonForStayRequest {

  @NotEmpty
  private String basketReference;
  @NotNull
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", required = true)
  private String reasonForStay;
  private List<String> reservationIds;
  private String arrivalDate;
  private String country;
  private String language;

}
