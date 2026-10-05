package uk.co.whitbread.content.infrastructure.rest.client.countries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COUNTIES_INFO_EXCEPTION;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.domain.model.countries.out.CountryInformation;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.countries.aem.adapter.CountriesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.in.CountriesResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.in.CountryInfoAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.countries.model.out.CountriesRequestAemDto;

@ExtendWith(MockitoExtension.class)
class CountriesInformationOutPortImplTest {

  @InjectMocks
  private CountriesOutPortImpl countriesOutPort;

  @Mock
  private CountriesAemClient countriesAEMClient;

  @Mock
  private CountriesRequestMapper countriesRequestMapper;

  @Mock
  private CountriesResponseMapper countriesResponseMapper;

  @Test
  void getCountries__ShouldReturnOK() {
    //Arrange
    when(countriesAEMClient.getCountries(any())).thenReturn(mockCountriesResponseAEMDto());
    when(countriesRequestMapper.toDto(any())).thenReturn(new CountriesRequestAemDto());
    when(countriesResponseMapper.toModel(any())).thenReturn(mockCountriesResponse());

    //Act
    var countries = countriesOutPort.getCountries(getCountriesRequest());

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

  @Test
  void getCountries_ShouldReturnResourceNotFound() {
    //Arrange
    var expectedMessage = "Unable to counties.";
    when(countriesRequestMapper.toDto(any())).thenReturn(new CountriesRequestAemDto());
    when(countriesAEMClient.getCountries(any())).thenThrow(
        new AemResponseException(AEM_COUNTIES_INFO_EXCEPTION, "Unable to counties.", new Exception()));
    var countriesRequest = getCountriesRequest();
    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> countriesOutPort.getCountries(countriesRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_COUNTIES_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_COUNTIES_INFO_EXCEPTION.getCode()));
  }

  private CountriesResponseAemDto mockCountriesResponseAEMDto() {
    var countryInfo = CountryInfoAemDto.builder()
        .countryCode("UK")
        .countryCodeIso("GB")
        .countryLegend("United Kingdom (the)")
        .passportRequired(false)
        .dialingCode("+44")
        .flagImg("/content/dam/global/flags/United-Kingdom.png")
        .nationality("Briton")
        .build();

    return CountriesResponseAemDto.builder()
        .countries(Collections.singletonList(countryInfo))
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

  private CountriesRequest getCountriesRequest() {
    return CountriesRequest.builder()
        .country("gb")
        .language("en")
        .site("leisure")
        .build();
  }
}
