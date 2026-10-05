package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CancelReservationRequestDto {

  @NotEmpty
  @Schema(required = true)
  private String basketReference;
  private List<String> reservationIds;

  @Schema(example = "LONSTM")
  private String hotelId;
  private String paymentOption;
  private String token;
}
