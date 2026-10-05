package uk.co.whitbread.content.infrastructure.rest.client.note.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.domain.ports.secondary.BusinessNotesOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter.BusinessNotesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesResponseMapper;

@RequiredArgsConstructor
@Slf4j
public class BusinessNotesOutPortImpl implements BusinessNotesOutPort {

  private final BusinessNotesAemClient businessNotesAemClient;
  private final BusinessNotesRequestMapper businessNotesRequestMapper;
  private final BusinessNotesResponseMapper businessNotesResponseMapper;

  @Override
  public BusinessNotesResponse getBusinessNotes(BusinessNotesRequest businessNotesRequest) {
    log.debug("Entered getBusinessNotes with lang={}", businessNotesRequest.getLang());
    var businessNotesRequestAem = businessNotesRequestMapper.toDto(businessNotesRequest);
    return businessNotesResponseMapper.toModel(
        businessNotesAemClient.getBusinessNotes(businessNotesRequestAem));
  }
}
