package uk.co.whitbread.payments.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payments.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PaymentMethodsRequest extends SelfValidation<PaymentMethodsRequest> {
  @NotEmpty
  String basketReference;
  @NotEmpty
  String language;
  @NotEmpty
  String country;
  UserType userType;
  String clientChannel;
  String bookingChannel;
  String authorization;
  boolean changePaymentBIC;
  String flow;

  public PaymentMethodsRequest(String basketReference, String language, String country,
                               UserType userType, String clientChannel, String bookingChannel, String authorization,
                               boolean changePaymentBIC, String flow) {
    this.basketReference = basketReference;
    this.language = language;
    this.country = country;
    this.userType = userType;
    this.clientChannel = clientChannel;
    this.bookingChannel = bookingChannel;
    this.authorization = authorization;
    this.changePaymentBIC = changePaymentBIC;
    this.flow = flow;
    this.validateSelf();
  }

}
