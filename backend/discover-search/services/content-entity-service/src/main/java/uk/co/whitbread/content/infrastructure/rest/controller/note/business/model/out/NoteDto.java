package uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {
  private String id;
  private String lang;
  private String value;
}
