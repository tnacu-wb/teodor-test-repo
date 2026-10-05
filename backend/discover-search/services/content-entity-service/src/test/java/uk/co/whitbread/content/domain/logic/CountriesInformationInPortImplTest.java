package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.domain.model.countries.out.CountryInformation;
import uk.co.whitbread.content.domain.ports.secondary.CountriesOutPort;

@ExtendWith(MockitoExtension.class)
class CountriesInformationInPortImplTest {

  @InjectMocks
  private CountriesInPortImpl countriesInPort;

  @Mock
  private CountriesOutPort countriesOutPort;


  @Test
  void getCountries__ShouldReturnOk() {
    //Arrange
    when(this.countriesOutPort.getCountries(createCountriesRequest())).thenReturn(mockCountriesResponse());

    //Act
    final var countries = countriesInPort.getCountries(createCountriesRequest());

    //Assert
    assertThat(countries, notNullValue());
    var countryInfo = countries.getCountries().get(0);
    assertThat(countryInfo.getCountryCode(), is("GB"));
    assertThat(countryInfo.getCountryCodeLegacy(), is("UK"));
    assertThat(countryInfo.getCountryName(), is("United Kingdom (the)"));
    assertThat(countryInfo.getPassportRequired(), is(false));
    assertThat(countryInfo.getDialingCode(), is("+44"));
    assertThat(countryInfo.getFlagSrc(), is("/content/dam/global/flags/United-Kingdom.png"));
    assertThat(countryInfo.getNationality(), is("Briton"));
  }

  private CountriesRequest createCountriesRequest() {
    return CountriesRequest.builder()
        .language("en")
        .country("gb")
        .site("leisure")
        .build();
  }

  private CountriesInformation mockCountriesResponse() {
    var countryInfo = CountryInformation.builder()
        .countryCode("GB")
        .countryCodeLegacy("UK")
        .countryName("United Kingdom (the)")
        .passportRequired(false)
        .dialingCode("+44")
        .flagSrc("/content/dam/global/flags/United-Kingdom.png")
        .nationality("Briton")
        .build();

    return CountriesInformation.builder()
        .countries(Collections.singletonList(countryInfo))
        .build();
  }
}
