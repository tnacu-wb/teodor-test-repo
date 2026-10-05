package uk.co.whitbread.payments.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest.Params;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

  @Mapping(target = "bookingChannel", source = "booking.channel")
  @Mapping(target = "currency", source = "payment.amount.currency")
  @Mapping(target = "amount", source = "payment.amount.minorUnits")
  @Mapping(target = "cardholderStreetAddress1", source = "payment.billing.address.line1")
  @Mapping(target = "cardholderStreetAddress2", source = "payment.billing.address.line2")
  @Mapping(target = "cardholderStreetAddress3", source = "payment.billing.address.line3")
  @Mapping(target = "cardholderStreetAddress4", source = "payment.billing.address.line4")
  @Mapping(target = "cardholderCountry", source = "payment.billing.address.countryCode")
  @Mapping(target = "cardholderZipCode", source = "payment.billing.address.postalCode")
  @Mapping(target = "cardholderNameFirst", source = "payment.billing.firstName")
  @Mapping(target = "cardholderNameLast", source = "payment.billing.lastName")
  @Mapping(target = "cardholderEmail", source = "payment.billing.email", qualifiedByName = "toEmail")
  @Mapping(target = "cardholderTelephone", source = "payment.billing.telephone")
  Params toParams(PaymentRequest paymentRequest);

  @Mapping(target = "trxAmountCurrencyCode", source = "payment.amount.currency")
  @Mapping(target = "cardholderAddressLine1", source = "payment.billing.address.line1")
  @Mapping(target = "cardholderAddressLine2", source = "payment.billing.address.line2")
  @Mapping(target = "cardholderAddressLine3", source = "payment.billing.address.line3")
  @Mapping(target = "cardholderAddressLine4", source = "payment.billing.address.line4")
  @Mapping(target = "cardholderAddressCountry", source = "payment.billing.address.countryCode")
  @Mapping(target = "cardholderAddressPostalCode", source = "payment.billing.address.postalCode")
  @Mapping(target = "cardholderFirstName", source = "payment.billing.firstName")
  @Mapping(target = "cardholderLastName", source = "payment.billing.lastName")
  @Mapping(target = "cardholderEmail", source = "payment.billing.email", qualifiedByName = "toEmail")
  @Mapping(target = "cardholderTelephone", source = "payment.billing.telephone")
  @Mapping(target = "cardExpiryMonth", source = "payment.card.expiryMonth")
  @Mapping(target = "cardExpiryYear", source = "payment.card.expiryYear")
  InitialiseRequest toInitialiseRequest(PaymentRequest paymentRequest);

  @Mapping(target = "request.params", source = "paymentRequest")
  NoCardReadTransactionRequest toNoCardReadTransactionRequest(PaymentRequest paymentRequest);

  @Named("toEmail")
  default String toEmail(String billingEmail) {
    String defaultEmail = "default@whitbread.com";

    return StringUtils.isEmpty(billingEmail) ? defaultEmail : billingEmail;
  }

}
