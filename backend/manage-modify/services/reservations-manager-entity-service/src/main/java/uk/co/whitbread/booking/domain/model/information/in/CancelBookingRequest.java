package uk.co.whitbread.booking.domain.model.information.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
public class CancelBookingRequest {

  @NotEmpty
  @Schema(required = true)
  private String bookingReference;

  private String basketReference;

  @Schema(example = "LONSTM", required = true)
  private String hotelId;

  private String sourceSystem;

  private String arrivalDate;

  private String paymentOption;

  private String token;
  private String country;
  private String language;
}
