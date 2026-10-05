package uk.co.whitbread.content.infrastructure.rest.controller.cookies;

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
import uk.co.whitbread.content.domain.ports.primary.CookiePoliciesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper.CookiePoliciesDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper.CookiePoliciesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.in.CookiePoliciesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.CookiePoliciesInformationDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CookiePoliciesController implements CookiePoliciesApiDocumentation {

  private final CookiePoliciesInPort cookiePoliciesInPort;
  private final CookiePoliciesRequestDtoMapper cookiePoliciesRequestDtoMapper;
  private final CookiePoliciesDtoMapper cookiePoliciesDtoMapper;

  @GetMapping(value = "/content/cookie-policies", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CookiePoliciesInformationDto> getCookiePolicies(
      @Valid @ParameterObject CookiePoliciesRequestDto cookiePoliciesRequestDto) {
    var request = cookiePoliciesRequestDtoMapper.toDomainModel(cookiePoliciesRequestDto);
    var cookiePoliciesDto = cookiePoliciesDtoMapper.toDto(
        cookiePoliciesInPort.getCookiePolicies(request));
    return ResponseEntity.status(HttpStatus.OK).body(cookiePoliciesDto);
  }
}
