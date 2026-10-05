package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GqtRoomDto {

  private String roomType;
  private BigDecimal amount;
  private int minNights;
  private int maxNights;
  private boolean cta;
  private long quantity;

}
