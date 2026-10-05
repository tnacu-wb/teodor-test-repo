package uk.co.whitbread.availabilitycacheservice.domain.model.los.rules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class LosRestriction {

  private LosRestrictionName losRule;
  /**
   * minNights : hotel has a restriction of minimum number of nights that the customer must book for Ex : Has to stay.
   * for 2 nights at a minimum
   **/
  private int minNights;
  /**
   * maxNights : hotel has a restriction of maximum number of nights that the customer can stay for Ex : Can stay til.
   * maximum of 3 nights (i,e 1, 2 or 3 nights stay allowed, 4 nights stay not allowed
   **/
  private int maxNights;
  /**
   * noNightsAllowed : hotel has a restriction of min & max number of nights that the customer can stay for Ex :.
   * Customer not allowed to stay for 2 or 3 nights, but can stay for 1 or 4 nights
   **/
  private int[] noNightsAllowed;
}
