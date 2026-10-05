package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

  private String id;
  private String area;
  private String code;
  private String description;
  private boolean screenNotification;
  private boolean printerNotification;

}
