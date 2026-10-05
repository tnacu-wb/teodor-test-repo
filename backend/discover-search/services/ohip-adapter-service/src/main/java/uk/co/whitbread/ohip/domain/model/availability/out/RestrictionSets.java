package uk.co.whitbread.ohip.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionSets {
  private RestrictionControl restrictionControl;
  private RestrictionStatus restrictionStatus;
  private ActualTimeSpan actualTimeSpan;
  private boolean onRequest;
  private String start;
  private String end;
  private boolean sunday;
  private boolean monday;
  private boolean tuesday;
  private boolean wednesday;
  private boolean thursday;
  private boolean friday;
  private boolean saturday;
}
