package uk.co.whitbread.availabilitycacheservice.domain.model.gqt;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {

  private String roomType;
  private BigDecimal amount;
  private int minNights;
  private int maxNights;
  private boolean cta;
  private long quantity;

}
