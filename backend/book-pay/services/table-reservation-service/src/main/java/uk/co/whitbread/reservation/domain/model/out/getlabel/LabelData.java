package uk.co.whitbread.reservation.domain.model.out.getlabel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelData {

  private String key;
  private String value;
}
