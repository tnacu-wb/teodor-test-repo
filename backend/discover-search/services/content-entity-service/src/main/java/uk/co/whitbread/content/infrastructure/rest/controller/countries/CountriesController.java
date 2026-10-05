package uk.co.whitbread.content.infrastructure.rest.controller.countries;

import static uk.co.whitbread.content.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.CountriesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper.CountriesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.mapper.CountriesResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.in.CountriesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.out.CountriesDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CountriesController implements CountriesApiDocumentation {

  private final CountriesInPort countriesInPort;
  private final CountriesRequestDtoMapper countriesRequestDtoMapper;
  private final CountriesResponseDtoMapper countriesResponseDtoMapper;

  @GetMapping(value = "/content/countries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CountriesDto> getCountries(
      @Valid @ParameterObject CountriesRequestDto countriesRequestDto) {

    log.debug("Request to get countries for {} {} reason for stay {} started.",
        sanitize(countriesRequestDto.getCountry()), sanitize(countriesRequestDto.getLanguage()),
        sanitize(countriesRequestDto.getSite()));

    var request = countriesRequestDtoMapper.toDomainModel(countriesRequestDto);
    var countriesDto = countriesResponseDtoMapper.toDto(
        countriesInPort.getCountries(request));

    return ResponseEntity.status(HttpStatus.OK).body(countriesDto);
  }
}
