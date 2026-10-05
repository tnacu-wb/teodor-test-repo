package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;

public interface BusinessNotesOutPort {
  BusinessNotesResponse getBusinessNotes(BusinessNotesRequest businessNotesRequest);
}
