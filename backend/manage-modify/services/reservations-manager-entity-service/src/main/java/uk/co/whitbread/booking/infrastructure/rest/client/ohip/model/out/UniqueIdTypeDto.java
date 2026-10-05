package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class UniqueIdTypeDto {

  private String id;
  private String type;
}
