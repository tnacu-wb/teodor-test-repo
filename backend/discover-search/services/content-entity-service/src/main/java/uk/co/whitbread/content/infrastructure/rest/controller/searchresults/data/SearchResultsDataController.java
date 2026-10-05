package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data;

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
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.LocalizationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.mapper.SearchResultsDataDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.SearchResultsDataDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SearchResultsDataController implements SearchResultsDataApiDocumentation {

  private final LocalizationRequestDtoMapper localizationRequestDtoMapper;
  private final ContentInPort contentInPort;
  private final SearchResultsDataDtoMapper searchResultsDataDtoMapper;


  @GetMapping(value = "/content/searchresults/data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchResultsDataDto> getSearchResultsData(
      @Valid @ParameterObject LocalizationDto localizationDto) {

    var localizationRequest = localizationRequestDtoMapper.toDomainModel(localizationDto);
    var searchResultsData = contentInPort.getSearchResultsData(localizationRequest);
    var response = searchResultsDataDtoMapper.toDtoModel(searchResultsData);

    return ResponseEntity.ok(response);
  }
}
