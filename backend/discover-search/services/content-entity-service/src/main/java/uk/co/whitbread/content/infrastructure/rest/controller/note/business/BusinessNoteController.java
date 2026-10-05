package uk.co.whitbread.content.infrastructure.rest.controller.note.business;

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
import uk.co.whitbread.content.domain.ports.primary.BusinessNotesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper.BusinessNotesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper.BusinessNotesResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.in.BusinessNotesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNotesResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class BusinessNoteController implements BusinessNoteApiDocumentation {

  private final BusinessNotesInPort businessNotesInPort;
  private final BusinessNotesRequestDtoMapper businessNotesRequestDtoMapper;
  private final BusinessNotesResponseDtoMapper businessNotesResponseDtoMapper;

  @GetMapping(value = "/content/businessNotes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BusinessNotesResponseDto> getBusinessNotes(
      @Valid @ParameterObject BusinessNotesRequestDto businessNotesRequestDto) {
    final var businessNotesRequest = businessNotesRequestDtoMapper.toDomainModel(businessNotesRequestDto);

    final var businessNotesResponseDto = businessNotesResponseDtoMapper.toDto(
        businessNotesInPort.getBusinessNotes(businessNotesRequest));

    return ResponseEntity.status(HttpStatus.OK).body(businessNotesResponseDto);
  }
}
