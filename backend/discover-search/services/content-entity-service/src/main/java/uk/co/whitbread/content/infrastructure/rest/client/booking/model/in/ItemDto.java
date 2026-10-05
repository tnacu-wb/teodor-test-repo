package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

  private String code;
  private String order;
  private String bartCode;

}
