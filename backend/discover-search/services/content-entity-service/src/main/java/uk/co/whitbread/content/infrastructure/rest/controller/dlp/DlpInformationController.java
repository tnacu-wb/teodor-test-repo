package uk.co.whitbread.content.infrastructure.rest.controller.dlp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.mapper.ControllerDlpInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out.DlpInformationDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class DlpInformationController implements DlpInformationApiDocumentation {

  private final ContentInPort contentInPort;
  private final ControllerDlpInformationMapper dlpInformationMapper;

  @Override
  @GetMapping(value = "/content/dlp-information", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DlpInformationDto> getDlpInformation(
      @Valid @ParameterObject DlpInformationRequestDto dlpInformationRequestDto) {

    var dlpInformationRequest = dlpInformationMapper.toDomainModel(dlpInformationRequestDto);
    var dlpInformation = contentInPort.getDlpInformation(dlpInformationRequest);
    var dlpInformationDto = dlpInformationMapper.toDto(dlpInformation);
    return ResponseEntity.ok(dlpInformationDto);
  }
}
