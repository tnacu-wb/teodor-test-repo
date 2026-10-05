package uk.co.whitbread.basket.domain.model.content.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessNotesResponse {
  List<BusinessNote> businessNotes;
  List<Note> footers;
  List<Note> headers;
  List<Note> allowances;
  List<Note> packages;
  List<Note> cardTypes;
}
