package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata;

import static uk.co.whitbread.content.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.PageDataInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.mapper.PageDataRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.PageDataRequestDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PageDataController implements PageDataApiDocumentation {

  private final PageDataInPort pageDataInPort;
  private final PageDataRequestDtoMapper pageDataRequestDtoMapper;

  @GetMapping(value = "/content/innb/pagedata", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Map<String, String>> getPageData(
      @Valid @ParameterObject PageDataRequestDto pageDataRequestDto) {

    log.info("Page info request parameters: dictionaries={}, country={}, language={}",
        sanitize(pageDataRequestDto.getDictionaries()),
            sanitize(pageDataRequestDto.getCountry()), sanitize(pageDataRequestDto.getLanguage()));

    final var pageDataRequest = pageDataRequestDtoMapper.toDomainModel(pageDataRequestDto);

    return pageDataInPort.getPageData(pageDataRequest);
  }
}
