package uk.co.whitbread.reservation.domain.model.out.occasion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Occasions {
  private String id;
  private String name;
  private Boolean available;

}
