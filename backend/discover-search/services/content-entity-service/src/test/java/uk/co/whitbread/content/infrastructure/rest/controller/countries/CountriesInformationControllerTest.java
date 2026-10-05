package uk.co.whitbread.content.infrastructure.rest.controller.countries;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.domain.model.countries.out.CountryInformation;
import uk.co.whitbread.content.domain.ports.primary.CountriesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper.CountriesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper.CountriesResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.in.CountriesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.out.CountriesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.out.CountryInformationDto;

@ExtendWith(MockitoExtension.class)
class CountriesInformationControllerTest {

  @InjectMocks
  private CountriesController countriesController;

  @Mock
  private CountriesInPort countriesInPort;

  @Mock
  private CountriesRequestDtoMapper countriesRequestDtoMapper;

  @Mock
  private CountriesResponseDtoMapper countriesDtoMapper;

  @Test
  void getCountries__ShouldReturnOK() {
    //Arrange
    var countriesRequestDto = getCountriesRequestDto();
    var countriesRequest = getCountriesRequest();
    when(countriesRequestDtoMapper.toDomainModel(countriesRequestDto))
        .thenReturn(countriesRequest);
    when(countriesInPort.getCountries(countriesRequest)).thenReturn(getCountries());
    when(countriesDtoMapper.toDto(getCountries())).thenReturn(getCountriesDto());

    //Act
    var request = countriesRequestDtoMapper.toDomainModel(countriesRequestDto);
    var countryInformationDto = countriesDtoMapper.toDto(
        countriesInPort.getCountries(request));
    final ResponseEntity<CountriesDto> response = countriesController.getCountries(countriesRequestDto);


    //Assert
    Assertions.assertNotNull(response.getBody());
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), countryInformationDto.getCountries(), response.getBody().getCountries());
  }

  private CountriesDto getCountriesDto() {
    var countryInfoDto = CountryInformationDto.builder()
        .countryCode("GB")
        .countryCodeLegacy("UK")
        .countryName("United Kingdom (the)")
        .passportRequired(false)
        .dialingCode("+44")
        .flagSrc("/content/dam/global/flags/United-Kingdom.png")
        .nationality("Briton")
        .build();

    return CountriesDto.builder()
        .countries(Collections.singletonList(countryInfoDto))
        .build();
  }

  private CountriesInformation getCountries() {
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

  private CountriesRequestDto getCountriesRequestDto() {
    return CountriesRequestDto.builder()
        .country("gb")
        .language("en")
        .site("leisure")
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
