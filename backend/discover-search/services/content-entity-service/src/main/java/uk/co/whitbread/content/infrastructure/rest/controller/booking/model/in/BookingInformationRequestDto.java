package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingInformationRequestDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String bookingFlowId;
  @NotEmpty
  private String hotelId;
  private List<String> ratePlanCodes;
  private String reservationRatePlanCode;

  public BookingInformationRequestDto(String country, String language, String bookingFlowId, String hotelId,
      List<String> ratePlanCodes, String reservationRatePlanCode) {
    this.country = country;
    this.language = language;
    this.bookingFlowId = bookingFlowId;
    this.hotelId = hotelId;
    this.ratePlanCodes = ratePlanCodes;
    this.reservationRatePlanCode = reservationRatePlanCode;
  }
}
