package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

    //Test for Phone Number
    @Test
    void testReturnErrorWhenPhoneNumberIsNull(){
        String res  = RequestValidator.isValidPhoneNumber(null);
        assertEquals("Phone number cannot be blank or null.", res);
    }

    @Test
    void testReturnErrorWhenPhoneNumberIsEmpty(){
        String res = RequestValidator.isValidPhoneNumber("");
        assertEquals("Phone number cannot be blank or null.", res);
    }

    @Test
    void testReturnErrorWhenPhoneNumberIsTooLong() {
        String res = RequestValidator.isValidPhoneNumber("+1234567891234565");
        assertEquals("Phone number length should be between 11 to 15 digits long.", res);
    }

    @Test
    void testReturnErrorWhenPhoneNumberIsTooShort(){
        String res = RequestValidator.isValidPhoneNumber("+1234");
        assertEquals("Phone number length should be between 11 to 15 digits long.", res);
    }

    @Test
    void testReturnErrorWhenPhoneNumberInvalid(){
        String res = RequestValidator.isValidPhoneNumber("+12344563756f");
        assertEquals("Phone number can only contain numeric digits (0-9) and starts with 0 or +.", res);
    }

    @Test
    void testReturnEmptyStringWhenPhoneNumberIsValid_starts_with_plus(){
        String res = RequestValidator.isValidPhoneNumber("+1234567891234");
        assertEquals("", res);
    }

    @Test
    void testReturnEmptyStringWhenPhoneNumberIsValid_starts_with_zero(){
        String res = RequestValidator.isValidPhoneNumber("01234567891");
        assertEquals("", res);
    }

    //Test for Name
    @Test
    void testReturnErrorWhenNameIsNull(){
        String res = RequestValidator.isValidName(null);
        assertEquals("Name cannot be blank or null.",res);
    }

    @Test
    void testReturnErrorWhenNameIsEmpty(){
        String res = RequestValidator.isValidName("");
        assertEquals("Name cannot be blank or null.", res);
    }

    @Test
    void testReturnErrorWhenNameIsTooShort(){
        String res = RequestValidator.isValidName("l");
        assertEquals("Name length should be between 2 and 100 characters.", res);
    }

    @Test
    void testReturnErrorWhenNameIsTooLong(){
        String res = RequestValidator.isValidName("dummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummydummy");
        assertEquals("Name length should be between 2 and 100 characters.",res);
    }

    @Test
    void testReturnEmptyWhenNameContainsDigits(){
        String res = RequestValidator.isValidName("John109826");
        assertEquals("Name can only contain alphabetic characters and spaces.", res);
    }

    @Test
    void testReturnEmptyWhenNameIsValid(){
        String res = RequestValidator.isValidName("John");
        assertEquals("", res);
    }


    //Test for Email Address
    @Test
    void testReturnErrorWhenEmailAddressIsNull(){
        String res  = RequestValidator.isValidEmailAddress(null);
        assertEquals("Email Address cannot be blank or null.", res);
    }

    @Test
    void testReturnErrorWhenEmailAddressIsEmpty(){
        String res = RequestValidator.isValidEmailAddress("");
        assertEquals("Email Address cannot be blank or null.",res);
    }

    @Test
    void testReturnErrorWhenEmailAddressPlainAddress() {
        String res = RequestValidator.isValidEmailAddress("testAddress");
        assertEquals("Email Address is not in a format", res);
    }

    @Test
    void testReturnErrorWhenEmailAddressMissingUser() {
        String res = RequestValidator.isValidEmailAddress("@testAddress.com");
        assertEquals("Email Address is not in a format", res);
    }

    @Test
    void testReturnErrorWhenEmailAddressMissingTopLevelDomain() {
        String res = RequestValidator.isValidEmailAddress("test@domain");
        assertEquals("Email Address is not in a format", res);
    }

    @Test
    void testReturnErrorWhenEmailAddressUnsupportedTopLevelDomain() {
        String res = RequestValidator.isValidEmailAddress("test@domain.corporate");
        assertEquals("Email Address is not in a format", res);
    }

    @Test
    void testReturnEmptyStringWhenEmailAddressIsValid(){
        String res = RequestValidator.isValidEmailAddress("testAccount@domain.com");
        assertEquals("", res);
    }
}
