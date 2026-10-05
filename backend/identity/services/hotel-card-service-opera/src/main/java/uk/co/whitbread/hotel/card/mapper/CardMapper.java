package uk.co.whitbread.hotel.card.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.BusinessPaymentCard;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;

@Mapper(componentModel = "spring")
public interface CardMapper {

  @Mapping(target = "cardNotPresentRequired", source = "request.cnpRequired")
  @Mapping(target = "cardNotPresent.businessAccountUsername", source = "request.cnpBusinessAccountUsername")
  @Mapping(target = "cardNotPresent.businessAccountPassword", source = "request.cnpBusinessAccountPassword")
  @Mapping(target = "token", source = "request.cardToken")
  @Mapping(target = "nameOnCard", source = "request.cardHolderName")
  @Mapping(target = "cardNumber", source = "maskedCardNumber")
  @Mapping(target = "billingAddress", expression = "java(toBusinessAddress(request.getBillingAddress()))")
  PaymentCardDetails toPaymentCardDetails(PaymentCard request, String maskedCardNumber);

  @Mapping(target = "addressLine1", source = "line1")
  @Mapping(target = "addressLine2", source = "line2")
  @Mapping(target = "addressLine3", source = "line3")
  @Mapping(target = "addressLine4", source = "line4")
  @Mapping(target = "addressLine5", source = "line5")
  @Mapping(target = "companyName", source = "companyName")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "countryCode", source = "countryCode")
  BusinessAddress toBusinessAddress(Address address);

  @InheritInverseConfiguration
  Address toAddress(BusinessAddress businessAddress);

  @Mapping(target = "cnpRequired", source = "request.cardNotPresentRequired")
  @Mapping(target = "cnpBusinessAccountUsername", source = "request.cardNotPresent.businessAccountUsername")
  @Mapping(target = "cnpBusinessAccountPassword", source = "request.cardNotPresent.businessAccountPassword")
  @Mapping(target = "cardHolderName", source = "request.nameOnCard")
  @Mapping(target = "billingAddress", expression = "java(toAddress(request.getBillingAddress()))")
  @Mapping(target = "cardToken", source = "token")
  PaymentCard toPaymentCard(GetPaymentCardDetailsResponse request);

  @Mapping(target = "cardToken", source = "token")
  PaymentCard fromBusinessPaymentCardToPaymentCard(BusinessPaymentCard businessPaymentCard);

  @Mapping(target = "paymentPreference.paymentCard", source = "paymentCard")
  @Mapping(target = "paymentPreference.paymentCard.cardNumber", source = "maskedCardNumber")
  @Mapping(target = "paymentPreference.paymentCard.token", source = "paymentCard.cardToken")
    GetEmployeeResponse addCustomerPaymentCard(@MappingTarget GetEmployeeResponse employeeResponse,
      PaymentCard paymentCard, String maskedCardNumber);
}