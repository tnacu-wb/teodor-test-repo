package uk.co.whitbread.content.infrastructure.rest.controller.footer;

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
import uk.co.whitbread.content.domain.ports.primary.FooterInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper.FooterRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper.FooterResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.in.FooterRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.FooterResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class FooterController implements FooterApiDocumentation {

  private final FooterRequestDtoMapper footerRequestDtoMapper;
  private final FooterInPort footerInPort;
  private final FooterResponseDtoMapper footerResponseDtoMapper;

  @GetMapping(value = "/content/footer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FooterResponseDto> getFooter(
      @Valid @ParameterObject FooterRequestDto footerRequestDto) {

    final var footerRequest = footerRequestDtoMapper.toModel(footerRequestDto);
    var footerInfoDto =
        footerResponseDtoMapper.toDto(footerInPort.getFooterInformation(footerRequest));

    return ResponseEntity.status(HttpStatus.OK).body(footerInfoDto);
  }
}
