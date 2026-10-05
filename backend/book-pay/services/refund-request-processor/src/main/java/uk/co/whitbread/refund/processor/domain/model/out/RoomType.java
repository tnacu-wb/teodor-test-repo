package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

  private String type;
  private String rate;
  private int adults;
}
