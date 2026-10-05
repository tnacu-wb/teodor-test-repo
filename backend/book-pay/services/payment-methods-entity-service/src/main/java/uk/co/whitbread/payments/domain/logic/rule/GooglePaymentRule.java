package uk.co.whitbread.payments.domain.logic.rule;


import static uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant.GOOGLE;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class GooglePaymentRule implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule GooglePaymentRule");
    var hotelId = request.getReservation().getHotelId();
    Optional<PaymentMethodsConfiguration> googlePaymentConfig =
        Optional.ofNullable(request.getPaymentMethodsConfiguration())
            .filter(list -> !list.isEmpty())
            .flatMap(list -> list.stream()
                //Checking Client Channel and Payment Method Code
                .filter(paymentMethodConfiguration -> GOOGLE.equalsIgnoreCase(paymentMethodConfiguration.getCode())
                    && paymentMethodConfiguration.getSupportedChannels().contains(request.getClientChannel()))
                .findFirst());
    if (googlePaymentConfig.isPresent()) {
      log.info("Google Pay enabled for hotel -> " + hotelId);
      request.getPaymentMethods().forEach(paymentMethod -> {
        if (Objects.nonNull(paymentMethod)
            && StringUtils.isNotBlank(paymentMethod.getType())
            && StringUtils.equalsIgnoreCase(GOOGLE, paymentMethod.getType())) {
          //Setting the Logo for the Payment Method
          paymentMethod.setLogoSrc(googlePaymentConfig.get().getLogo());

          //Enabling & Disabling PAY_NOW or PAY_ON_ARRIVAL
          paymentMethod.getPaymentOptions().forEach(paymentOption ->
              paymentOption.setEnabled(googlePaymentConfig.get().getSupportedBookingTypes()
                  .contains(paymentOption.getType())));

          //Setting the list of Accepted Cards for Google Pay
          var cardSource = request.resolveCardSource(GOOGLE.toUpperCase());
          paymentMethod.setAcceptedCardTypes(cardSource.stream()
              .filter(acceptedCardType ->
                  StringUtils.isNotEmpty(googlePaymentConfig.get().getSupportedCards())
                      && StringUtils.isNotEmpty(acceptedCardType.getType())
                      && googlePaymentConfig.get().getSupportedCards().contains(acceptedCardType.getType()))
              .toList());
          paymentMethod.setPaymentProvider(request.resolvePaymentProvider(GOOGLE.toUpperCase()));
        }
      });
    } else {
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.removeIf(paymentMethod -> StringUtils.equalsIgnoreCase(GOOGLE,
          paymentMethod.getType()));
      log.info("Google Pay NOT allowed for hotel -> " + hotelId);
    }
    return request;
  }
}