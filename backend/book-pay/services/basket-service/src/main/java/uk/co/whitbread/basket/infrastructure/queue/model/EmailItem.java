package uk.co.whitbread.basket.infrastructure.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailItem {

  private String type;
  private String sourceId;
  private String sourceSystemId;

}
