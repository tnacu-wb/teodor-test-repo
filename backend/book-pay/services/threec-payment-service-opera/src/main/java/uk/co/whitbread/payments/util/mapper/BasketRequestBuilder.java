package uk.co.whitbread.payments.util.mapper;

import java.net.URI;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.booking.basket.BasketRequest;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;

@Component
@Slf4j
public class BasketRequestBuilder {

  public BasketRequest buildBasketRequest(PaymentsSchema paymentsSchema, String paymentStatus,
      String threeDSIndicator, UnleashWrapper<FeatureFlag> unleashWrapper) {

        var payment = paymentsSchema.getPayment() == null ? new Payment() : paymentsSchema.getPayment();
        var booking = paymentsSchema.getBooking() == null ? new Booking() : paymentsSchema.getBooking();
        var billing = payment.getBilling() == null ? new Billing() : payment.getBilling();
        var address = billing.getAddress() == null ? new Address() : billing.getAddress();
        var providerResponse = paymentsSchema.getProviderResponse() == null ? new ProviderResponse() : paymentsSchema.getProviderResponse();
        var threeCResponse = providerResponse.getThreeCResponse() == null ? new ThreeCResponse() : providerResponse.getThreeCResponse();

    return BasketRequest.builder()
                .reference(paymentsSchema.getBooking().getReference())
                .paymentId(paymentsSchema.getPaymentId())
                .paymentStatus(paymentStatus)
                .bookingReference(paymentsSchema.getBooking().getBookingReference())
                .countryCode(address.getCountryCode())
                .language(booking.getLanguage())
                .firstName(billing.getFirstName())
                .lastName(billing.getLastName())
                .channel(booking.getChannel())
                .last4Digits(threeCResponse.getLast4Digits())
                .cardSchemeId(threeCResponse.getCardSchemeId())
                .returnCode(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMockPaymentReturnCode())
                              ? String.valueOf(new String(Base64.getDecoder().decode(billing.getFirstName())).length() * 3)
                              : threeCResponse.getProviderResult())
                .token(threeCResponse.getToken())
                .expiry(threeCResponse.getExpiry())
                .fraudCheckDecision(threeCResponse.getFraudCheckDecision())
                .threeDSIndicator(threeDSIndicator)
                .build();
    }

    /**
     * This method is to transform the environment passed from basket: '<a href="https://www.qablue.premierinn.digital">...</a>'
     * in to a Whitbread environment: 'qablue'. The use of catch Throwable here to stop any kind of exception being
     * thrown and allow the booking flow to fail naturally when trying to call the basket.
     *
     * @param environment - environment variable passed in from basket service
     * @return Whitbread environment
     */
    public String processEnvironment(String environment) {
        try {
            var uri = new URI(environment);
            var path = uri.getHost().split("\\.");
            return path[1];
        } catch (Exception e) {
            log.info("Error processing environment: {}", e.getMessage());
            return "";
        }
    }
}
