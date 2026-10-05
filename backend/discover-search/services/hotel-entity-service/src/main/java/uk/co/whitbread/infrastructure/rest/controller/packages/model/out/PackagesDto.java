package uk.co.whitbread.infrastructure.rest.controller.packages.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesDto {

  private List<MealDto> meals;
  private List<ExtrasDto> extrasItems;

}
