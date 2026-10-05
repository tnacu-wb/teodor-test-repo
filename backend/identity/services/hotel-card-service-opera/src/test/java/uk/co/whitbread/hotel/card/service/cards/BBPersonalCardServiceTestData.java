package uk.co.whitbread.hotel.card.service.cards;

import uk.co.whitbread.hotel.card.model.AddressDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.BusinessPaymentCard;
import uk.co.whitbread.shared.cdh.model.BusinessPaymentPreference;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;

public class BBPersonalCardServiceTestData {

    protected static EmployeeAccountRequest testUpdatePaymentCardSuccess_expectedUpdateRequest(PaymentCardBBPersonal paymentCard,
        String employeeAccountId, String companyAccountId) {
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
        return EmployeeAccountRequest.builder()
                .paymentPreference(BusinessPaymentPreference.builder()
                        .paymentCard(BusinessPaymentCard.builder()
                                .cardNumber(paymentCard.getCardNumber())
                                .token(paymentCard.getCardToken())
                                .billingAddress(billingAddress)
                                .cardHolderName(paymentCard.getCardHolderName())
                                .cardType(paymentCard.getCardType())
                                .expiryDate(paymentCard.getExpiryDate())
                                .cnpRequired(paymentCard.getCnpRequired())
                                .cnpBusinessAccountUsername(paymentCard.getCnpBusinessAccountUsername())
                                .cnpBusinessAccountPassword(paymentCard.getCnpBusinessAccountPassword())
                                .build())
                        .build())
            .employeeAccountId(employeeAccountId)
            .companyAccountId(companyAccountId)
            .build();
    }

    protected static PaymentCardBBPersonal testMapPaymentCard_expectedPaymentCard(PaymentCardDTO paymentCardDTO, boolean cnpRequired) {
        return PaymentCardBBPersonal.builder()
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
                .companyAccountId(paymentCardDTO.getCompanyAccountId())
                .employeeAccountId(paymentCardDTO.getEmployeeAccountId())
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
