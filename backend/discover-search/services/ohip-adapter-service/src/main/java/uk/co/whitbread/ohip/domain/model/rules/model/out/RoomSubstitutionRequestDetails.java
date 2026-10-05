package uk.co.whitbread.ohip.domain.model.rules.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSubstitutionRequestDetails {

  private Integer adults;
  private Integer children;
  private String roomType;
  private Boolean cotRequired;
  private String pms;

}
