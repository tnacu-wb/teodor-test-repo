package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import static uk.co.whitbread.content.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.ExtrasDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.LabelsRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.MultipleLabelsRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out.ExtrasLabelDto;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class LabelsController implements LabelsApiDocumentation {

  private final ContentInPort contentInPort;
  private final LabelsRequestDtoMapper labelsRequestDtoMapper;
  private final MultipleLabelsRequestDtoMapper multipleLabelsRequestDtoMapper;
  private final ExtrasDtoMapper extrasDtoMapper;

  @GetMapping(value = "/content/labels", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Map<String, String>> getLabels(
      @Valid @ParameterObject MultipleLabelsRequestDto multipleLabelsRequestDto) {

    final var domainContentRequest =
        multipleLabelsRequestDtoMapper.toDomainModel(multipleLabelsRequestDto);

    return contentInPort.getMultipleLabels(domainContentRequest);
  }

  @SuppressWarnings("squid:S6856")
  @GetMapping(value = "/content/labels/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, String> getLabels(
      @Valid @ParameterObject LabelsRequestDto labelsRequestDto) {

    log.info("Category labels request parameters: category={}, labels={}, country={}, language={}",
        sanitize(labelsRequestDto.getCategory()), sanitize(labelsRequestDto.getLabels()),
        sanitize(labelsRequestDto.getCountry()), sanitize(labelsRequestDto.getLanguage()));

    final var domainContentRequest = labelsRequestDtoMapper.toDomainModel(labelsRequestDto);

    return contentInPort.getLabels(domainContentRequest);
  }

  @GetMapping(value = "/content/labels/extras", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ExtrasLabelDto> getExtras(@Valid @ParameterObject LocalizationDto localization) {
    var extras = contentInPort.getExtras(localization.getCountry(), localization.getLanguage());
    return ResponseEntity.ok(extrasDtoMapper.toDto(extras));
  }
}
