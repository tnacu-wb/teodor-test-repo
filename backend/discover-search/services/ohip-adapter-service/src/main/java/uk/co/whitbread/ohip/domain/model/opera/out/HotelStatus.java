package uk.co.whitbread.ohip.domain.model.opera.out;

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
public class HotelStatus {

  private String hotelId;
  private String pmsSource;
  private boolean onSale;

}
