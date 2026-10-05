package uk.co.whitbread.basket.domain.model.payments.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class PaymentRequest implements SelfValidation<PaymentRequest> {

  private String requestId;
  private String tmpBasketRef;
  private Payment payment;
  private Booking booking;
  private BusinessAccount businessAccount;
  private List<String> specialRequests;
  private List<String> bookingNotes;
  private CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails;
  private boolean isCiol;
  private boolean isSecureBooking;
  private boolean useCache;

  public PaymentRequest(String requestId, String tmpBasketRef, Payment payment, Booking booking,
      BusinessAccount businessAccount, List<String> specialRequests, List<String> bookingNotes,
      CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails, boolean isCiol,
      boolean isSecureBooking, boolean useCache) {
    this.requestId = requestId;
    this.tmpBasketRef = tmpBasketRef;
    this.payment = payment;
    this.booking = booking;
    this.businessAccount = businessAccount;
    this.specialRequests = specialRequests;
    this.bookingNotes = bookingNotes;
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    this.isCiol = isCiol;
    this.isSecureBooking = isSecureBooking;
    this.useCache = useCache;
    this.validateSelf();
  }
}
