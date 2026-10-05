package uk.co.whitbread.domain.model.packages.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtrasCutoff {

  private String code;

  private Integer cutOffMinutesBooking;

  private Integer cutOffMinutesCIOL;

  private String message;
}
