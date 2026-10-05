package uk.co.whitbread.hotel.account.fixture;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.shared.cdh.model.ContactDetail;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.PaymentCard;
import uk.co.whitbread.shared.cdh.model.PaymentPreference;

public class CdhCustomerFixture {

    public static CustomerAccountRequest createCustomerAccountRequest(String email) {
        return CustomerAccountRequest.builder()
                .contactDetail(ContactDetail.builder().email(email).build())
                .build();
    }

    public static CustomerAccountResponse createCustomerAccountResponse(String customerAccountId) {
        return CustomerAccountResponse.builder()
            .customerAccountId(customerAccountId)
            .build();
    }

    public static GetCustomerAccountResponse createGetCustomerAccountResponse(String customerAccountId) {
        return GetCustomerAccountResponse.builder()
                .customerAccountId(customerAccountId)
                .build();
    }

    public static GetCustomerAccountResponse createGetCustomerAccountResponse(
        String customerAccountId, String email) {
        return GetCustomerAccountResponse.builder()
            .customerAccountId(customerAccountId)
            .contactDetail(ContactDetail.builder().email(email).build())
            .paymentPreference(new PaymentPreference(
                    true,
                    PaymentCard.builder()
                            .cardNumber("4111111111111103")
                            .cardHolderName("test")
                            .expiryDate("01/27")
                            .cardType("VI")
                            .build()
                    )
            )
            .build();
    }

    public static CustomerRequest createCustomerRequest(String email) {
        return createCustomerRequest(email, "British");
    }

    public static CustomerRequest createCustomerRequest(String email, String nationality) {
        CustomerRequest customerRequest = new CustomerRequest();
        uk.co.whitbread.hotel.account.model.PaymentPreference paymentPreference = new uk.co.whitbread.hotel.account.model.PaymentPreference();
        paymentPreference.setPaymentCard(
                uk.co.whitbread.hotel.account.model.PaymentCard.builder()
                        .cardNumber("4111111111111103")
                        .cardHolderName("test")
                        .expiryDate("01/27")
                        .cardType("VI")
                        .billingAddress(uk.co.whitbread.hotel.account.model.BillingAddress.builder().line1("line 1").countryCode("RO").postCode("100200").build())
                        .build());
        customerRequest.setPaymentPreference(paymentPreference);

        uk.co.whitbread.hotel.account.model.ContactDetail contactDetail = random(uk.co.whitbread.hotel.account.model.ContactDetail.class);

        contactDetail.setTelephone("07701234567");
        contactDetail.setEmail(email);
        contactDetail.setMobile("07701234567");
        contactDetail.setNationality(nationality);
        contactDetail.getAddress().setCountryCode("GB");

        customerRequest.setContactDetail(contactDetail);

        return customerRequest;
    }
}
