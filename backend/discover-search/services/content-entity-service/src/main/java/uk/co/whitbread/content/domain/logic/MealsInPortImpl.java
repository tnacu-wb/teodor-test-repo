package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.domain.ports.primary.MealsInPort;
import uk.co.whitbread.content.domain.ports.secondary.MealsOutPort;

@Slf4j
@RequiredArgsConstructor
public class MealsInPortImpl implements MealsInPort {

  private final MealsOutPort mealsOutPort;

  @Override
  public MealsInfoResponse getMealsInfo(MealsRequest mealsRequest) {
    return mealsOutPort.getMealsInfo(mealsRequest);
  }

}
