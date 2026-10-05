package uk.co.whitbread.availabilitycacheservice.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommonRatePlan {

  private String code;

  private String classification;

  private int minNights;

  private int maxNights;

}
