package uk.co.whitbread.hotel.card.mapper;

import org.mapstruct.*;
import uk.co.whitbread.hotel.card.model.AddressDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.shared.cdh.model.*;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;

@Mapper(componentModel = "spring")
public interface CDHMapper {

    @Mapping(target = "additionalGuests", source = "response.additionalGuests")
    @Mapping(target = "bookingPreference", source = "response.bookingPreference")
    @Mapping(target = "contactDetail", source = "response.contactDetail")
    @Mapping(target = "bartGuestHistoryCreation", source = "response.bartGuestHistoryCreation")
    @Mapping(target = "bartGuestHistoryNumber", source = "response.bartGuestHistoryNumber")
    CustomerAccountRequest toCustomerAccountRequest(GetCustomerAccountResponse response,
                                                    PaymentCardPIPersonal card);

    @AfterMapping
    default void toCustomerAccountRequestAfterMapping(GetCustomerAccountResponse response, PaymentCardPIPersonal card,
                                                      @MappingTarget CustomerAccountRequest.CustomerAccountRequestBuilder request) {
        request.paymentPreference(PaymentPreference.builder()
                .electronicInvoiceRequired(response.getPaymentPreference() != null
                        && response.getPaymentPreference().getElectronicInvoiceRequired())
                .paymentCard(PaymentCard.builder()
                        .cardHolderName(card.getCardHolderName())
                        .cardNumber(card.getCardNumber())
                        .cardType(card.getCardType())
                        .expiryDate(card.getExpiryDate())
                        .token(card.getCardToken())
                        .billingAddress(toCdhAddress(card.getBillingAddress()))
                        .build())
                .build());
    }

    @Mapping(target = "addressLine1", source = "line1")
    @Mapping(target = "addressLine2", source = "line2")
    @Mapping(target = "addressLine3", source = "line3")
    @Mapping(target = "addressLine4", source = "line4")
    @Mapping(target = "addressLine5", source = "line5")
    @Named("toCdhAddress")
    Address toCdhAddress(AddressDTO address);

    EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse response);

    @Mapping(target = "paymentPreference.paymentCard.cardNumber", source = "card.cardNumber")
    @Mapping(target = "paymentPreference.paymentCard.token", source = "card.cardToken")
    @Mapping(target = "paymentPreference.paymentCard.billingAddress", source = "card.billingAddress",
            qualifiedByName = "toCdhBusinessAddress")
    @Mapping(target = "paymentPreference.paymentCard.cardHolderName", source = "card.cardHolderName")
    @Mapping(target = "paymentPreference.paymentCard.cardType", source = "card.cardType")
    @Mapping(target = "paymentPreference.paymentCard.expiryDate", source = "card.expiryDate")
    @Mapping(target = "paymentPreference.paymentCard.cnpRequired", source = "card.cnpRequired")
    @Mapping(target = "paymentPreference.paymentCard.cnpBusinessAccountUsername", source = "card.cnpBusinessAccountUsername")
    @Mapping(target = "paymentPreference.paymentCard.cnpBusinessAccountPassword", source = "card.cnpBusinessAccountPassword")
    EmployeeAccountRequest updateWithCardDetails(PaymentCardBBPersonal card,
                                                 @MappingTarget EmployeeAccountRequest employeeRequest);


    @Mapping(target = "cardNotPresentRequired", source = "card.cnpRequired")
    @Mapping(target = "cardNotPresent.businessAccountUsername", source = "card.cnpBusinessAccountUsername")
    @Mapping(target = "cardNotPresent.businessAccountPassword", source = "card.cnpBusinessAccountPassword")
    @Mapping(target = "token", source = "card.cardToken")
    @Mapping(target = "nameOnCard", source = "card.cardHolderName")
    @Mapping(target = "billingAddress", source = "card.billingAddress", qualifiedByName = "toCdhBusinessAddress")
    PaymentCardDetails toPaymentCardDetails(PaymentCardBBCentral card);

    @Mapping(target = "addressLine1", source = "line1")
    @Mapping(target = "addressLine2", source = "line2")
    @Mapping(target = "addressLine3", source = "line3")
    @Mapping(target = "addressLine4", source = "line4")
    @Mapping(target = "addressLine5", source = "line5")
    @Named("toCdhBusinessAddress")
    BusinessAddress toCdhBusinessAddress(AddressDTO address);

    @Mapping(target = "billingAddress", source = "newCard.billingAddress")
    @Mapping(target = "cardLabel", source = "newCard.cardLabel")
    @Mapping(target = "cardNotPresent", source = "newCard.cardNotPresent")
    @Mapping(target = "cardNotPresentRequired", source = "newCard.cardNotPresentRequired")
    @Mapping(target = "cardNumber", source = "cdhCard.cardNumber")
    @Mapping(target = "token", source = "cdhCard.token")
    @Mapping(target = "cardType", source = "cdhCard.cardType")
    @Mapping(target = "expiryDate", source = "cdhCard.expiryDate")
    @Mapping(target = "nameOnCard", source = "cdhCard.nameOnCard")
    PaymentCardDetails updateWithCdhDetails(PaymentCardDetails newCard, GetPaymentCardDetailsResponse cdhCard);
}
