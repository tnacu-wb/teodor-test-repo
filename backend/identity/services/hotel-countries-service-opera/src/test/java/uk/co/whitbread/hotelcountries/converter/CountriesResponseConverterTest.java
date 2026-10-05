package uk.co.whitbread.hotelcountries.converter;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotelcountries.model.CountriesResponse;
import uk.co.whitbread.hotelcountries.model.Country;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;
import uk.co.whitbread.hotelcountries.model.aem.AemCountry;

import java.util.Arrays;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountriesResponseConverterTest {

    private static final String AEMCODE1 = "AEMCODE1";
    private static final String AEMCODE2 = "AEMCODE2";
    private static final String DIALING_CODE1 = "DIALING1";
    private static final String DIALING_CODE2 = "DIALING2";
    private static final String IMG = "IMG";
    private static final String LEGEND = "LEGEND1";
    private static final String ISOCODE1 = "CODE1";
    private static final String ISOCODE2 = "CODE2";

    @InjectMocks
    private CountriesResponseConverter countriesResponseConverter;

    @Mock
    private AemCountries aemCountries;

    @BeforeEach
    public void setUp() {


        when(aemCountries.getCountries()).thenReturn(Arrays.asList(AemCountry.builder().countryCode(AEMCODE1)
                        .countryCode(AEMCODE1)
                        .dialingCode(DIALING_CODE1)
                        .flagImg(IMG)
                        .isoCode(ISOCODE1)
                        .legend(LEGEND)
                        .passportRequired(true)
                        .build(),
                AemCountry.builder().countryCode(AEMCODE2)
                        .countryCode(AEMCODE2)
                        .dialingCode(DIALING_CODE2)
                        .flagImg(IMG)
                        .isoCode(ISOCODE2)
                        .legend(LEGEND)
                        .passportRequired(false)
                        .build()));
    }

    @Test
    public void convertToCountriesResponse_mapAemFields() {

        aemCountries.getCountries().get(0).setCountryCode(AEMCODE1);
        aemCountries.getCountries().get(1).setCountryCode(AEMCODE2);

        CountriesResponse countryResponse = countriesResponseConverter.convertToCountriesResponse(aemCountries);
        SoftAssertions softAssertions = new SoftAssertions();

        Country country1 = countryResponse.getCountries().get(0);
        Country country2 = countryResponse.getCountries().get(1);

        createCountryAssertions(softAssertions, country1, AEMCODE1, ISOCODE1, LEGEND, true, DIALING_CODE1, IMG);
        createCountryAssertions(softAssertions, country2, AEMCODE2, ISOCODE2, LEGEND, false, DIALING_CODE2, IMG);

    }

    private void createCountryAssertions(SoftAssertions softAssertions, Country country, String expectedBartCode, String expectedIsoCode, String expectedLegend,
                                         boolean passportRequired, String expectedDialingCode, String expectedFlagImg) {
        softAssertions.assertThat(country.getCountryCode()).as("countryCode").isEqualTo(expectedBartCode);
        softAssertions.assertThat(country.getCountryCodeISO()).as("countryCodeISO").isEqualTo(expectedIsoCode);
        softAssertions.assertThat(country.getCountryLegend()).as("countryLegend").isEqualTo(expectedLegend);
        softAssertions.assertThat(country.isPassportRequired()).as("passportRequired").isEqualTo(passportRequired);
        softAssertions.assertThat(country.getDialingCode()).as("dialingCode").isEqualTo(expectedDialingCode);
        softAssertions.assertThat(country.getFlagImg()).as("flagImg").isEqualTo(expectedFlagImg);
        softAssertions.assertAll();

    }
}