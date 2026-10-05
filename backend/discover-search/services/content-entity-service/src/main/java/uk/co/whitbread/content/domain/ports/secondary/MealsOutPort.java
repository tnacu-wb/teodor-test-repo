package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;

public interface MealsOutPort {

  MealsInfoResponse getMealsInfo(MealsRequest mealsRequest);

}
