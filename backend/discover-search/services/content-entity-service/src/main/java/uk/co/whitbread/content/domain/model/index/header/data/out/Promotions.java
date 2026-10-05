package uk.co.whitbread.content.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotions {

  private Integer maxRoomsAmend;
  private Integer maxRooms;
  private Integer numberOfNights;
  private String promoCode;
  private String page;
  private Boolean enabled;
}
