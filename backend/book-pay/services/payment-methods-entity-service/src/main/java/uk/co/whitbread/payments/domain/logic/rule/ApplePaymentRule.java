package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant.APPLE;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.payments.domain.model.out.PaymentMethodsConfiguration;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class ApplePaymentRule implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule ApplePaymentRule");
    var hotelId = request.getReservation().getHotelId();
    Optional<PaymentMethodsConfiguration> applePaymentConfig =
        Optional.ofNullable(request.getPaymentMethodsConfiguration())
            .filter(list -> !list.isEmpty())
            .flatMap(list -> list.stream()
                //Checking Client Channel and Payment Method Code
                .filter(paymentMethodConfiguration -> APPLE.equalsIgnoreCase(paymentMethodConfiguration.getCode())
                    && paymentMethodConfiguration.getSupportedChannels().contains(request.getClientChannel()))
                .findFirst());

    if (applePaymentConfig.isPresent()) {
      log.info("Apple Pay enabled for hotel -> " + hotelId);
      request.getPaymentMethods().forEach(paymentMethod -> {
        if (Objects.nonNull(paymentMethod)
            && StringUtils.isNotBlank(paymentMethod.getType())
            && StringUtils.equalsIgnoreCase(APPLE, paymentMethod.getType())) {
          //Setting the Logo for the Payment Method
          paymentMethod.setLogoSrc(applePaymentConfig.get().getLogo());

          //Enabling & Disabling PAY_NOW or PAY_ON_ARRIVAL
          paymentMethod.getPaymentOptions().forEach(paymentOption ->
              paymentOption.setEnabled(applePaymentConfig.get().getSupportedBookingTypes()
                  .contains(paymentOption.getType())));

          //Setting the list of Accepted Cards for Apple Pay
          var cardSource = request.resolveCardSource(APPLE.toUpperCase());
          paymentMethod.setAcceptedCardTypes(cardSource.stream()
              .filter(acceptedCardType ->
                  StringUtils.isNotEmpty(applePaymentConfig.get().getSupportedCards())
                      && StringUtils.isNotEmpty(acceptedCardType.getType())
                      && applePaymentConfig.get().getSupportedCards().contains(acceptedCardType.getType()))
              .toList());
          paymentMethod.setPaymentProvider(request.resolvePaymentProvider(APPLE.toUpperCase()));
        }
      });
    } else {
      var paymentMethods = request.getPaymentMethods();
      paymentMethods.removeIf(paymentMethod -> StringUtils.equalsIgnoreCase(APPLE,
          paymentMethod.getType()));
      log.info("Apple Pay NOT allowed for hotel -> " + hotelId);
    }
    return request;
  }
}