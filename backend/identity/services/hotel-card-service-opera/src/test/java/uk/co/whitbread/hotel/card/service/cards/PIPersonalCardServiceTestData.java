package uk.co.whitbread.hotel.card.service.cards;

import uk.co.whitbread.hotel.card.model.AddressDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.shared.cdh.model.*;

import java.util.Optional;

public class PIPersonalCardServiceTestData {

    protected static CustomerAccountRequest testUpdatePaymentCardSuccess_expectedUpdateRequest(PaymentCardPIPersonal paymentCard, GetCustomerAccountResponse response) {
        var billingAddress = new BusinessAddress();
        billingAddress.setCompanyName(paymentCard.getBillingAddress().getCompanyName());
        billingAddress.setCountryCode(paymentCard.getBillingAddress().getCountryCode());
        billingAddress.setCountry(paymentCard.getBillingAddress().getCountry());
        billingAddress.setAddressLine1(paymentCard.getBillingAddress().getLine1());
        billingAddress.setAddressLine2(paymentCard.getBillingAddress().getLine2());
        billingAddress.setAddressLine3(paymentCard.getBillingAddress().getLine3());
        billingAddress.setAddressLine4(paymentCard.getBillingAddress().getLine4());
        billingAddress.setAddressLine5(paymentCard.getBillingAddress().getLine5());
        billingAddress.setPostCode(paymentCard.getBillingAddress().getPostCode());
        billingAddress.setType(paymentCard.getBillingAddress().getType());
        return CustomerAccountRequest.builder()
                .additionalGuests(response.getAdditionalGuests())
                .bookingPreference(response.getBookingPreference())
                .contactDetail(response.getContactDetail())
                .bartGuestHistoryCreation(response.getBartGuestHistoryCreation())
                .bartGuestHistoryNumber(response.getBartGuestHistoryNumber())
                .paymentPreference(PaymentPreference.builder()
                        .electronicInvoiceRequired(Optional.ofNullable(response.getPaymentPreference()).map(PaymentPreference::getElectronicInvoiceRequired).orElse(false))
                        .paymentCard(PaymentCard.builder()
                                .cardHolderName(paymentCard.getCardHolderName())
                                .cardNumber(paymentCard.getCardNumber())
                                .cardType(paymentCard.getCardType())
                                .expiryDate(paymentCard.getExpiryDate())
                                .token(paymentCard.getCardToken())
                                .billingAddress(billingAddress)
                                .build())
                        .build())
                .build();
    }

    protected static PaymentCardPIPersonal testMapPaymentCard_expectedPaymentCard(PaymentCardDTO paymentCardDTO, boolean cnpRequired) {
        return PaymentCardPIPersonal.builder()
                .billingAddress(AddressDTO.builder()
                        .type(paymentCardDTO.getBillingAddress().getType())
                        .countryCode(paymentCardDTO.getBillingAddress().getCountryCode())
                        .country(paymentCardDTO.getBillingAddress().getCountry())
                        .line1(paymentCardDTO.getBillingAddress().getLine1())
                        .line2(paymentCardDTO.getBillingAddress().getLine2())
                        .line3(paymentCardDTO.getBillingAddress().getLine3())
                        .line4(paymentCardDTO.getBillingAddress().getLine4())
                        .line5(paymentCardDTO.getBillingAddress().getLine5())
                        .postCode(paymentCardDTO.getBillingAddress().getPostCode())
                        .build())
                .customerAccountId(paymentCardDTO.getCustomerAccountId())
                .userEmail(paymentCardDTO.getUserEmail())
                .cnpBusinessAccountPassword(cnpRequired ? paymentCardDTO.getCnpBusinessAccountPassword() : null)
                .cnpBusinessAccountUsername(cnpRequired ? paymentCardDTO.getCnpBusinessAccountUsername() : null)
                .cnpRequired(paymentCardDTO.getCnpRequired())
                .cardToken(paymentCardDTO.getCardToken())
                .cardType(paymentCardDTO.getCardType())
                .expiryDate(paymentCardDTO.getExpiryDate())
                .cardHolderName(paymentCardDTO.getCardHolderName())
                .build();
    }
}
