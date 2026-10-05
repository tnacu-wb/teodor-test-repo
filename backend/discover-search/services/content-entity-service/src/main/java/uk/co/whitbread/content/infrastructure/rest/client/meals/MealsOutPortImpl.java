package uk.co.whitbread.content.infrastructure.rest.client.meals;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.domain.ports.secondary.MealsOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.meals.adapter.MealsAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class MealsOutPortImpl implements MealsOutPort {

  private final MealsRequestMapper mealsRequestMapper;
  private final MealsResponseMapper mealsResponseMapper;
  private final MealsAemClient aemClient;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "MealsInfoCache")
  public MealsInfoResponse getMealsInfo(MealsRequest mealsRequest) {
    log.debug("Entered getMealsInfo with country={}, language={}, hotelId={}",
        mealsRequest.getCountry(), mealsRequest.getLanguage(), mealsRequest.getHotelId());
    var mealsInfoRequestAem = mealsRequestMapper.toDto(mealsRequest);
    return mealsResponseMapper.toModel(aemClient.getMealsInfo(mealsInfoRequestAem));
  }
}
