package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header;

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
import uk.co.whitbread.content.domain.ports.primary.HeaderInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper.HeaderRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper.HeaderResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.in.HeaderRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HeaderResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class HeaderController implements HeaderApiDocumentation {

  private final HeaderRequestDtoMapper headerRequestDtoMapper;
  private final HeaderInPort headerInPort;
  private final HeaderResponseDtoMapper headerResponseDtoMapper;

  @GetMapping(value = "/content/innb/header", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HeaderResponseDto> getHeaderInfo(@Valid @ParameterObject HeaderRequestDto headerRequestDto) {

    log.debug("Request to get InnBusiness content information with country={} and language={} started.",
        sanitize(headerRequestDto.getCountry()), sanitize(headerRequestDto.getLanguage()));

    final var headerRequest = headerRequestDtoMapper.toModel(headerRequestDto);
    var headerInfoDto = headerResponseDtoMapper.toDto(headerInPort.getHeaderInformation(headerRequest));

    return ResponseEntity.status(HttpStatus.OK).body(headerInfoDto);
  }
}
