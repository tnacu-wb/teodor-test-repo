package uk.co.whitbread.rules.agent.domain.logic;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.agent.domain.model.in.OccupancySupplementRequest;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplementResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.OccupancySupplementInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.OccupancySupplementRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class OccupancySupplementInPortImpl implements OccupancySupplementInPort {

  private final OccupancySupplementRepositoryOutPort occupancySupplementRepository;

  @Override
  public OccupancySupplementResponse getOccupancySupplementPricing(
      OccupancySupplementRequest occupancySupplementRequest) {

    var hotelId = occupancySupplementRequest.getHotelId();
    return occupancySupplementRepository.findRule(hotelId)
        .map(rule -> OccupancySupplementResponse.builder()
            .hotelId(rule.getHotelId())
            .pricing(rule.getPricing())
            .build())
        .orElse(OccupancySupplementResponse.builder()
            .hotelId(hotelId)
            .pricing(BigDecimal.ZERO)
            .build());
  }
}
