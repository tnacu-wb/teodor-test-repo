package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;

public interface BusinessNotesInPort {
  BusinessNotesResponse getBusinessNotes(BusinessNotesRequest businessNotesRequest);
}
