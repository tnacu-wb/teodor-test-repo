package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealsIncludedDto {

  private String mealName;
  private boolean dinner;
  private boolean breakfast;

}
