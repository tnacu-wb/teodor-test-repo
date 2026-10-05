package uk.co.whitbread.hotel.account.fixture;

import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.CustomerResponse;

public class CustomerRequestFixture {
    private static final String OLD_PASSWORD = "oldPassword1";

    public static CustomerRequest createCustomerRequest(String email, String password) {
        CustomerRequest customerRequest = new CustomerRequest();
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setEmail(email);
        customerRequest.setContactDetail(contactDetail);
        customerRequest.setNewPassword(password);
        customerRequest.setPassword(OLD_PASSWORD);
        return customerRequest;
    }

    public static CustomerResponse createCustomerResponse(String customerId) {
        return CustomerResponse.builder()
            .customerId(customerId)
            .success(true)
            .build();
    }
}
