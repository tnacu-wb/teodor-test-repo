package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCodesDto {
  private String code;
  private Integer order;
}
