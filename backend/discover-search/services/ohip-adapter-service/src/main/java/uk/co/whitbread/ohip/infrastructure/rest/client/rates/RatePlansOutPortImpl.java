package uk.co.whitbread.ohip.infrastructure.rest.client.rates;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodes;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.NegotiatedRatesMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.PromotionCodeMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlanInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlansMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;

@RequiredArgsConstructor
@Slf4j
public class RatePlansOutPortImpl implements RatePlansOutPort {

  private final RatePlansMapper ratePlansMapper;
  private final NegotiatedRatesMapper negotiatedRatesMapper;
  private final OhipRatePlansClient ohipRatePlansClient;
  private final RatePlanInfoMapper ratePlanInfoMapper;
  private final PromotionCodeMapper promotionCodeMapper;

  @Override
  public RatePlansResponse getRatePlans(List<String> ratePlans, String hotelId) {
    return ratePlansMapper.toDomainModel(
        ohipRatePlansClient.getRatePlans(ratePlans, hotelId).block());
  }

  @Override
  public NegotiatedRatesResponse getNegotiatedRatesForProfileId(final String profileId) {
    return negotiatedRatesMapper.toDomainModel(
        ohipRatePlansClient.getNegotiatedRatesForProfileId(profileId).block());
  }

  @Override
  public RatePlanInfoResponse getRatePlanInfo(String ratePlan, String hotelId) {
    return ratePlanInfoMapper.toDomainModel(
        ohipRatePlansClient.getRatePlanInfo(ratePlan, hotelId));
  }

  @Override
  public List<PromotionCodeResponse> getPromotionCode(List<String> promotionCode, String hotelId) {
    PropertyPromotionCodes response =
        ohipRatePlansClient.getPromotionCode(promotionCode, hotelId);

    return response.getPropertyPromotionCodes()
        .getPropertyPromotionCodes()
        .stream()
        .map(promotionCodeMapper::toDomainModel)
        .toList();

  }
}
