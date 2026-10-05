package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.domain.ports.primary.BusinessNotesInPort;
import uk.co.whitbread.content.domain.ports.secondary.BusinessNotesOutPort;

@RequiredArgsConstructor
public class BusinessNotesInPortImpl implements BusinessNotesInPort {

  private final BusinessNotesOutPort businessNotesOutPort;

  @Override
  public BusinessNotesResponse getBusinessNotes(BusinessNotesRequest businessNotesRequest) {
    return businessNotesOutPort.getBusinessNotes(businessNotesRequest);
  }
}
