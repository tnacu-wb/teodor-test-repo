package uk.co.whitbread.domain.model.opera.out;

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
  private Boolean onSale;
  private String pmsSource;

}
