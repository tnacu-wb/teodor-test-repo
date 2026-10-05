package uk.co.whitbread.avail.business.events.infrastructure.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SelectAllFromHotelAcRoomAndRateResponse {

  private String id;
  private String hotelCode;
  private String availDate;
  private Boolean avail;
  private String rate;
  private BigDecimal amount;
  private int quantity;

}
