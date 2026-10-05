package uk.co.whitbread.basket.domain.logic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentResponseWebhookMapper {
  @Mapping(source = "reference", target = "booking.reference")
  @Mapping(source = "channel", target = "booking.channel")
  @Mapping(source = "language", target = "booking.language")
  @Mapping(source = "cardSchemeId", target = "providerResponse.threecResponse.cardSchemeId")
  @Mapping(source = "last4Digits", target = "providerResponse.threecResponse.last4Digits")
  @Mapping(source = "token", target = "providerResponse.threecResponse.token")
  @Mapping(source = "expiry", target = "providerResponse.threecResponse.expiry")
  @Mapping(source = "fraudCheckDecision", target = "providerResponse.threecResponse.fraudCheckDecision")
  @Mapping(source = "firstName", target = "payment.billing.firstName")
  @Mapping(source = "lastName", target = "payment.billing.lastName")
  @Mapping(source = "threeDSIndicator", target = "providerResponse.threecResponse.threeDSIndicator")
  PaymentResponse toPaymentsResponseModel(PaymentsConfirmation paymentsConfirmation);
}
