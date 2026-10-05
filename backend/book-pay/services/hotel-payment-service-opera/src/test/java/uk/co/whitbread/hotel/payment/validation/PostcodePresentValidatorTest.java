package uk.co.whitbread.hotel.payment.validation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.payment.model.Address;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PostcodePresentValidatorTest {

    private PostcodePresentValidator sut;

    @BeforeEach
    public void setUp() throws Exception {
        sut = new PostcodePresentValidator();
    }

    @Test
    public void shouldHandleNullInput() throws Exception {
        //Given
        Address address = null;

        //When
        boolean valid = sut.isValid(address, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldPassWhenPostCodeIsPresent() throws Exception {
        //Given
        Address address = new Address();
        address.setCountryCode("GB");
        address.setPostcode("PostCode");

        //When
        boolean valid = sut.isValid(address, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldPassWhenCountryIsNotGB() throws Exception {
        //Given
        Address address = new Address();
        address.setCountryCode("DE");

        //When
        boolean valid = sut.isValid(address, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldFailWhenPostCodeIsNotPresentForCountryGB() throws Exception {
        //Given
        Address address = new Address();
        address.setCountryCode("GB");

        //When
        boolean valid = sut.isValid(address, null);

        //Then
        assertThat(valid, is(false));
    }
}