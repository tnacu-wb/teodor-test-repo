package uk.co.whitbread.basket.infrastructure.queue.processor;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAuthorisedEventPaymentResponseMapper {

  @Mapping(source = "paymentsConfirmation.paymentId", target = "paymentId")
  @Mapping(source = "paymentsConfirmation.paymentStatus", target = "paymentStatus")
  @Mapping(source = "paymentsConfirmation.bookingReference", target = "booking.reference")
  @Mapping(source = "paymentsConfirmation.channel", target = "booking.channel")
  @Mapping(source = "paymentsConfirmation.language", target = "booking.language")
  @Mapping(source = "paymentsConfirmation.cardSchemeId", target = "providerResponse.threecResponse.cardSchemeId")
  @Mapping(source = "paymentsConfirmation.last4Digits", target = "providerResponse.threecResponse.last4Digits")
  @Mapping(source = "paymentsConfirmation.token", target = "providerResponse.threecResponse.token")
  @Mapping(source = "paymentsConfirmation.expiry", target = "providerResponse.threecResponse.expiry")
  @Mapping(source = "paymentsConfirmation.fraudCheckDecision",
      target = "providerResponse.threecResponse.fraudCheckDecision")
  @Mapping(source = "paymentsConfirmation.firstName", target = "payment.billing.firstName")
  @Mapping(source = "paymentsConfirmation.lastName", target = "payment.billing.lastName")
  @Mapping(source = "paymentsConfirmation.threeDSIndicator",
      target = "providerResponse.threecResponse.threeDSIndicator")
  PaymentResponse toPaymentsResponseModel(PaymentsConfirmation paymentsConfirmation, Basket basket);

  @AfterMapping
  default void populateLanguageFromBasket(
      PaymentsConfirmation paymentsConfirmation,
      Basket basket,
      @MappingTarget PaymentResponse paymentResponse) {
    if (paymentsConfirmation != null && StringUtils.isNotBlank(paymentsConfirmation.getLanguage())) {
      return;
    }
    if (basket == null || basket.getItems() == null) {
      return;
    }

    var languageFromBasket = basket.getItems().stream()
        .map(BasketItem::getDetails)
        .filter(Objects::nonNull)
        .map(this::extractLanguage)
        .filter(StringUtils::isNotBlank)
        .findFirst()
        .orElse(null);
    if (StringUtils.isBlank(languageFromBasket) || paymentResponse.getBooking() == null) {
      return;
    }
    paymentResponse.getBooking().setLanguage(languageFromBasket);
  }

  private String extractLanguage(Map<String, String> details) {
    var language = details.get("language");
    if (StringUtils.isNotBlank(language)) {
      return language;
    }
    language = details.get("LANGUAGE");
    if (StringUtils.isNotBlank(language)) {
      return language;
    }
    return details.get("lang");
  }
}
