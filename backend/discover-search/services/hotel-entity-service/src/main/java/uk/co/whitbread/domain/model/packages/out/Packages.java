package uk.co.whitbread.domain.model.packages.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Packages {

  private List<Meal> meals;
  private List<ExtrasDto> extrasItems;

}
