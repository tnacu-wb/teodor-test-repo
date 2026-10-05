package uk.co.whitbread.content.domain.model.booking.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

  private String code;
  private String order;
  private String bartId;

}
