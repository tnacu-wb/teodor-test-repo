package uk.co.whitbread.hotel.payment.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.payment.model.Booker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TelephoneOrMobilePresentValidatorTest {

    private TelephoneOrMobilePresentValidator sut;

    @BeforeEach
    public void setUp() throws Exception {
        sut = new TelephoneOrMobilePresentValidator();
    }

    @Test
    public void shouldHandleNullInput() throws Exception {
        //Given
        Booker booker = null;

        //When
        boolean valid = sut.isValid(booker, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldPassForTelephone() throws Exception {
        //Given
        Booker booker = new Booker();
        booker.setTelephoneNumber("any");

        //When
        boolean valid = sut.isValid(booker, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldPassForMobile() throws Exception {
        //Given
        Booker booker = new Booker();
        booker.setMobileNumber("any");

        //When
        boolean valid = sut.isValid(booker, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldFailWhenNoNumbersArePresent() throws Exception {
        //Given
        Booker booker = new Booker();

        //When
        boolean valid = sut.isValid(booker, null);

        //Then
        assertThat(valid, is(false));
    }
}