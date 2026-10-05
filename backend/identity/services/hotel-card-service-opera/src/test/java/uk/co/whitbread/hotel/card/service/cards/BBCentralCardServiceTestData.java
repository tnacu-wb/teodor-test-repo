package uk.co.whitbread.hotel.card.service.cards;

import uk.co.whitbread.hotel.card.model.AddressDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.CardNotPresent;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;

public class BBCentralCardServiceTestData {

    protected static PaymentCardDetails testAddNewPaymentCardSuccess_expectedPaymentCardDetails(PaymentCardBBCentral paymentCard) {
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
        return PaymentCardDetails.builder()
                .billingAddress(billingAddress)
                .cardLabel(paymentCard.getCardLabel())
                .cardNotPresent(CardNotPresent.builder()
                        .businessAccountUsername(paymentCard.getCnpBusinessAccountUsername())
                        .businessAccountPassword(paymentCard.getCnpBusinessAccountPassword())
                        .build())
                .cardNotPresentRequired(paymentCard.getCnpRequired())
                .cardNumber(paymentCard.getCardNumber())
                .token(paymentCard.getCardToken())
                .cardType(paymentCard.getCardType())
                .expiryDate(paymentCard.getExpiryDate())
                .nameOnCard(paymentCard.getCardHolderName())
                .build();
    }

    protected static PaymentCardDetails testEditNewPaymentNoneCardSuccess_expectedPaymentCardDetails(PaymentCardBBCentral paymentCard, GetPaymentCardDetailsResponse paymentCardDetailsResponse) {
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
        return PaymentCardDetails.builder()
                .billingAddress(billingAddress)
                .cardLabel(paymentCard.getCardLabel())
                .cardNotPresent(CardNotPresent.builder()
                        .businessAccountUsername(paymentCard.getCnpBusinessAccountUsername())
                        .businessAccountPassword(paymentCard.getCnpBusinessAccountPassword())
                        .build())
                .cardNotPresentRequired(paymentCard.getCnpRequired())
                .cardNumber(paymentCardDetailsResponse.getCardNumber())
                .token(paymentCardDetailsResponse.getToken())
                .cardType(paymentCardDetailsResponse.getCardType())
                .expiryDate(paymentCardDetailsResponse.getExpiryDate())
                .nameOnCard(paymentCardDetailsResponse.getNameOnCard())
                .build();
    }

    protected static PaymentCardBBCentral testMapPaymentCard_expectedPaymentCard(PaymentCardDTO paymentCardDTO, boolean cnpRequired) {
        return PaymentCardBBCentral.builder()
                .billingAddress(AddressDTO.builder()
                        .type(paymentCardDTO.getBillingAddress().getType())
                        .companyName(paymentCardDTO.getBillingAddress().getCompanyName())
                        .countryCode(paymentCardDTO.getBillingAddress().getCountryCode())
                        .country(paymentCardDTO.getBillingAddress().getCountry())
                        .line1(paymentCardDTO.getBillingAddress().getLine1())
                        .line2(paymentCardDTO.getBillingAddress().getLine2())
                        .line3(paymentCardDTO.getBillingAddress().getLine3())
                        .line4(paymentCardDTO.getBillingAddress().getLine4())
                        .line5(paymentCardDTO.getBillingAddress().getLine5())
                        .postCode(paymentCardDTO.getBillingAddress().getPostCode())
                        .build())
                .cardId(paymentCardDTO.getCardId())
                .companyId(paymentCardDTO.getCompanyAccountId())
                .userEmail(paymentCardDTO.getUserEmail())
                .cardLabel(paymentCardDTO.getCardLabel())
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
