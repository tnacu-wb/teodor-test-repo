package uk.co.whitbread.reservation.domain.model.out.outlets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Features {
  private boolean bookableAreas;

}
