package uk.co.whitbread.hotel.account.fixture;

import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.Customer;

public class CustomerFixture {

    public static Customer createCustomer(String email) {
        Customer customer = new Customer();
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setEmail(email);
        customer.setContactDetail(contactDetail);
        return customer;
    }
}
