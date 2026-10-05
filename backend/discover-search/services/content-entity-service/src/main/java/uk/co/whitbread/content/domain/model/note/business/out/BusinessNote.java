package uk.co.whitbread.content.domain.model.note.business.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessNote {
  private String id;
  private String lang;
  private String allow;
  private String deny;
}
