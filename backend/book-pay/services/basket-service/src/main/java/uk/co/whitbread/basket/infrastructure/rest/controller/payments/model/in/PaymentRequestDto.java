package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;


import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidCompanyQuestionAndAnswerDetails;

@Data
@Builder
@NoArgsConstructor
public class PaymentRequestDto implements SelfValidation<PaymentRequestDto> {

  private String requestId;
  private String tmpBasketRef;
  private PaymentDto payment;
  private BookingDto booking;
  private BusinessAccountDto businessAccount;
  private List<String> specialRequests;
  private List<String> bookingNotes;

  @ValidCompanyQuestionAndAnswerDetails
  private CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails;
  private Boolean isCiol;
  private Boolean isSecureBooking;
  private Boolean useCache;

  public PaymentRequestDto(String requestId, String tmpBasketRef, PaymentDto payment, BookingDto booking,
      BusinessAccountDto businessAccount, List<String> specialRequests, List<String> bookingNotes,
      CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails, Boolean isCiol,
      Boolean isSecureBooking, Boolean useCache) {
    this.requestId = requestId;
    this.tmpBasketRef = tmpBasketRef;
    this.payment = payment;
    this.booking = booking;
    this.businessAccount = businessAccount;
    this.payment.getBilling().getAddress().setChannel(booking.getChannel());
    this.payment.getBilling().getAddress().validateSelf();
    this.specialRequests = specialRequests;
    this.bookingNotes = bookingNotes;
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    this.isCiol = isCiol;
    this.isSecureBooking = isSecureBooking;
    this.useCache = useCache;
    this.validateSelf();
  }
}
