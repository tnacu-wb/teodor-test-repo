package uk.co.whitbread.content.domain.model.booking.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class BookingInformationRequest implements SelfValidation<BookingInformationRequest> {

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


  public BookingInformationRequest(String country, String language, String bookingFlowId, String hotelId,
      List<String> ratePlanCodes, String reservationRatePlanCode) {
    this.country = country;
    this.language = language;
    this.bookingFlowId = bookingFlowId;
    this.hotelId = hotelId;
    this.ratePlanCodes = ratePlanCodes;
    this.reservationRatePlanCode = reservationRatePlanCode;
    this.validateSelf();
  }
}
