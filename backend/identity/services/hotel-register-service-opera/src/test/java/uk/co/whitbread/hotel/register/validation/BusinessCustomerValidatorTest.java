package uk.co.whitbread.hotel.register.validation;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.exceptions.BusinessValidationException;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BusinessCustomerValidatorTest {

   private final BusinessCustomerValidator businessCustomerValidator = new BusinessCustomerValidator();

    @Test
    public void ShouldPassWithAlternativeNameAndTelephonePopulated(){
        Customer customer = new Customer();
        customer.setAlternativeName("company");
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setTelephone("7789776665");
        customer.setContactDetail(contactDetail);

        businessCustomerValidator.validate(customer);
    }

    @Test
    public void ShouldThrowValidationExceptionWithTelephoneEmpty(){
        Customer customer = new Customer();
        customer.setAlternativeName("company");
        ContactDetail contactDetail = new ContactDetail();
        customer.setContactDetail(contactDetail);

        assertThrows(BusinessValidationException.class,
                () -> businessCustomerValidator.validate(customer),
                "telephone may not be empty");
    }

    @Test
    public void ShouldThrowValidationExceptionWithAlternativeNameEmpty(){
        Customer customer = new Customer();
        ContactDetail contactDetail = new ContactDetail();
        contactDetail.setTelephone("7789776665");
        customer.setContactDetail(contactDetail);

        assertThrows(BusinessValidationException.class,
                () -> businessCustomerValidator.validate(customer),
                "alternativeName may not be empty");

    }
}