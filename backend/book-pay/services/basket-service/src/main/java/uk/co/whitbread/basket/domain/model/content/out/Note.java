package uk.co.whitbread.basket.domain.model.content.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {

  private String id;
  private String lang;
  private String value;
}
