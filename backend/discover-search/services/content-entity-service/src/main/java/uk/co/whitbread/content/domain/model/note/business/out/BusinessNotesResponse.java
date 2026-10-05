package uk.co.whitbread.content.domain.model.note.business.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessNotesResponse {
  private List<BusinessNote> businessNotes;
  private List<Note> headers;
  private List<Note> footers;
  private List<Note> packages;
  private List<Note> cardTypes;
  private List<Note> allowances;
}
