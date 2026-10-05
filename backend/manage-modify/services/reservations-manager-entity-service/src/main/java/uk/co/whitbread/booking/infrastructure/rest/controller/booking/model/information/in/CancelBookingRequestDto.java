package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in;

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
public class CancelBookingRequestDto {

  @NotEmpty
  @Schema(required = true)
  private String bookingReference;
  private String basketReference;

  @Schema(example = "LONSTM")
  private String hotelId;

  private String sourceSystem;

  private String arrivalDate;

  private String paymentOption;

  private String token;

  private String language;

  private String country;
}
