package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;

public interface MealsInPort {

  MealsInfoResponse getMealsInfo(MealsRequest mealsRequest);

}
