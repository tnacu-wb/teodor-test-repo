package uk.co.whitbread.content.infrastructure.rest.controller.header.data;

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
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.IndexHeaderDataDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.IndexHeaderDataRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.in.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.IndexHeaderDataDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class IndexHeaderDataController implements IndexHeaderDataApiDocumentation {

  private final ContentInPort contentInPort;
  private final IndexHeaderDataRequestDtoMapper indexHeaderDataRequestDtoMapper;
  private final IndexHeaderDataDtoMapper indexHeaderDataDtoMapper;


  @GetMapping(value = "/content/header/data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IndexHeaderDataDto> getIndexHeaderData(
      @Valid @ParameterObject IndexHeaderDataRequestDto indexHeaderDataRequestDto) {

    var indexHeaderDataRequest = indexHeaderDataRequestDtoMapper
        .toDomainModel(indexHeaderDataRequestDto);
    var indexHeaderData = contentInPort.getIndexHeaderData(indexHeaderDataRequest);
    var domainContentRequest = indexHeaderDataDtoMapper.toDtoModel(indexHeaderData);

    return ResponseEntity.ok(domainContentRequest);
  }
}
