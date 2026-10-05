package uk.co.whitbread.basket.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
  private String area;
  private String code;
  private String description;
  private String id;
  private Boolean printerNotification;
  private Boolean screenNotification;
}
