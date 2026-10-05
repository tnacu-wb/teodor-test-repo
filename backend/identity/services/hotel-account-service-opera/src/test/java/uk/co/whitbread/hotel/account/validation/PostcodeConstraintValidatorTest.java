package uk.co.whitbread.hotel.account.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.properties.CountryCodesWithPostcodesProperties;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostcodeConstraintValidatorTest {

    private static final String COUNTRY_CODE_GB = "GB";
    private static final String COUNTRY_CODE_DE = "DE";

    private PostcodeConstraintValidator sut;

    @Mock
    private CountryCodesWithPostcodesProperties countryCodesWithPostcodesPropertiesMock;

    @BeforeEach
    void setUp() {
        sut = new PostcodeConstraintValidator(countryCodesWithPostcodesPropertiesMock);

        ArrayList<String> countryCodes = new ArrayList<>();
        countryCodes.add(COUNTRY_CODE_GB);
        countryCodes.add(COUNTRY_CODE_DE);

        when(countryCodesWithPostcodesPropertiesMock.getCountryCodeList()).thenReturn(countryCodes);
    }

    @Test
    void shouldReturnTrueIfAddressIsNull() {

        Address address = null;

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(true));
    }


    @Test
    void shouldReturnTrueIfCountryCodeIsNull() {

        Address address = new Address();
        address.setCountryCode(null);

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(true));
    }

    @Test
    void shouldReturnTrueIfCountryCodeListIsNull() {

        when(countryCodesWithPostcodesPropertiesMock.getCountryCodeList()).thenReturn(null);

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_GB);

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(true));
    }


    @Test
    void shouldReturnFalseWhenCountryCodeIsGbAndPostCodeIsNull() {

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_GB);
        address.setPostCode(null);

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(false));
    }

    @Test
    void shouldReturnFalseWhenCountryCodeIsGbAndPostCodeIsEmpty() {

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_GB);
        address.setPostCode("");

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(false));
    }

    @Test
    void shouldReturnFalseWhenCountryCodeIsDeAndPostCodeIsNull() {

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_DE);
        address.setPostCode(null);

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(false));
    }

    @Test
    void shouldReturnFalseWhenCountryCodeIsDeAndPostCodeIsEmpty() {

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_DE);
        address.setPostCode("");

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(false));
    }

    @Test
    void shouldReturnTrueWhenCountryCodeAndPostCodeIsValid() {

        Address address = new Address();
        address.setCountryCode(COUNTRY_CODE_GB);
        address.setPostCode("AB1 2CD");

        boolean valid = sut.isValid(address, null);

        assertThat(valid, is(true));
    }
}
