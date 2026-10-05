package uk.co.whitbread.ohip.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionControl {
  private boolean house;
  private String roomType;
  private String roomClass;
  private String ratePlanCode;
  private String ratePlanCategory;
}
