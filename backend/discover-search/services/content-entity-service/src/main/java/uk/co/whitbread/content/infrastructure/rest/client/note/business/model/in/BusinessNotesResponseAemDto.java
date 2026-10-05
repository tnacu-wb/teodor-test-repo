package uk.co.whitbread.content.infrastructure.rest.client.note.business.model.in;

import java.util.List;
import lombok.Data;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNoteDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.NoteDto;

@Data
public class BusinessNotesResponseAemDto {
  private List<BusinessNoteDto> businessNotes;
  private List<NoteDto> headers;
  private List<NoteDto> footers;
  private List<NoteDto> packages;
  private List<NoteDto> cardTypes;
  private List<NoteDto> allowances;
}
