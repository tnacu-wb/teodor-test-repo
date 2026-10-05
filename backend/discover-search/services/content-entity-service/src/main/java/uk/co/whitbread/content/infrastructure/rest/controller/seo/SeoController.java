package uk.co.whitbread.content.infrastructure.rest.controller.seo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.SeoInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper.SeoRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper.SeoResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.in.SeoRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.SeoResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeoController implements SeoApiDocumentation {

  private final SeoRequestDtoMapper seoRequestDtoMapper;
  private final SeoInPort seoInPort;
  private final SeoResponseDtoMapper seoResponseDtoMapper;

  @GetMapping(value = SEO_INFO_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SeoResponseDto> getSeo(
      @Valid @ParameterObject SeoRequestDto seoRequestDto) {
    final var seoRequest = seoRequestDtoMapper.toModel(seoRequestDto);
    var seoInfoDto =
        seoResponseDtoMapper.toDto(seoInPort.getSeoInformation(seoRequest));

    return ResponseEntity.status(HttpStatus.OK).body(seoInfoDto);
  }
}
