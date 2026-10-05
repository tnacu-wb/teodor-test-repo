package uk.co.whitbread.ohip.domain.model.availability.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealsIncluded {

  private String mealName;
  private boolean breakfast;
  private boolean dinner;
}
