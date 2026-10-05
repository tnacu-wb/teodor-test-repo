package uk.co.whitbread.hotel.register.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.model.Address;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PostcodeConstraintValidatorTest {


    private PostcodeConstraintValidator sut;

    @BeforeEach
    public void setUp() throws Exception {
        sut = new PostcodeConstraintValidator();
    }

    @Test
    public void shouldReturnTrueIfAddressIsNull() throws Exception {

        Address address = null;

        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(true));
    }


    @Test
    public void shouldReturnTrueIfCountryCodeIsNull() throws Exception {

        Address address = new Address();
        address.setCountryCode(null);

        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(true));
    }

    @Test
    public void shouldReturnFalseWhenCountryCodeIsGbAndPostCodeIsNull() throws Exception {

        Address address = new Address();
        address.setCountryCode("GB");
        address.setPostCode(null);

        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(false));
    }

    @Test
    public void shouldReturnFalseWhenCountryCodeIsGbAndPostCodeIsEmpty() throws Exception {

        Address address = new Address();
        address.setCountryCode("GB");
        address.setPostCode("");


        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(false));
    }

    @Test
    public void shouldReturnFalseWhenCountryCodeIsDeAndPostCodeIsNull() throws Exception {

        Address address = new Address();
        address.setCountryCode("DE");
        address.setPostCode(null);

        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(false));
    }

    @Test
    public void shouldReturnFalseWhenCountryCodeIsDeAndPostCodeIsEmpty() throws Exception {

        Address address = new Address();
        address.setCountryCode("DE");
        address.setPostCode("");


        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(false));
    }

    @Test
    public void shouldReturnTrueWhenCountryCodeAndPostCodeIsValid() throws Exception {

        Address address = new Address();
        address.setCountryCode("GB");
        address.setPostCode("AB1 2CD");


        boolean valid = sut.isValid(address, null);


        assertThat(valid, is(true));
    }
}
