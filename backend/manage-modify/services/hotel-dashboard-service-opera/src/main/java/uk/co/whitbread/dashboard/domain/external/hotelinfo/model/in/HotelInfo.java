package uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfo {

  private String name;
  private String brand;
  private InfoMap map;
  private List<InfoImage> images;
  private InfoAddress address;

}