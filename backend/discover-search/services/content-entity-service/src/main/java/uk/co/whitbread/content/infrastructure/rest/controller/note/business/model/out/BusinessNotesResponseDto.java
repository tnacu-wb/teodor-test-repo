package uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out;

import java.util.List;
import lombok.Data;

@Data
public class BusinessNotesResponseDto {
  private List<BusinessNoteDto> businessNotes;
  private List<NoteDto> headers;
  private List<NoteDto> footers;
  private List<NoteDto> packages;
  private List<NoteDto> cardTypes;
  private List<NoteDto> allowances;
}
