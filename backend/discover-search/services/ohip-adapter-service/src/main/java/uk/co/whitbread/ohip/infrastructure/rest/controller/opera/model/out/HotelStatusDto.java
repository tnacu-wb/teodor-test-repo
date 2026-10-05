package uk.co.whitbread.ohip.infrastructure.rest.controller.opera.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HotelStatusDto {

  private String hotelId;
  private boolean onSale;
  private String pmsSource;

}
