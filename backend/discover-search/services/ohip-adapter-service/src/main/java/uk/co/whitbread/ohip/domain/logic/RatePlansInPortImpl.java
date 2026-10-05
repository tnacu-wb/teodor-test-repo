package uk.co.whitbread.ohip.domain.logic;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_NO_PROMO_CODE_EXCEPTION;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.exceptions.UnavailableRatesException;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.domain.ports.primary.RatePlansInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;

@Slf4j
@RequiredArgsConstructor
public class RatePlansInPortImpl implements RatePlansInPort {

  private final RatePlansOutPort ratePlansOutPort;

  @Override
  public RatePlansResponse getRatePlans(List<String> ratePlans, String hotelId) {
    return ratePlansOutPort.getRatePlans(ratePlans, hotelId);
  }

  @Override
  public NegotiatedRatesResponse getNegotiatedRatesForProfileId(final String profileId) {
    return ratePlansOutPort.getNegotiatedRatesForProfileId(profileId);
  }

  @Override
  public RatePlanInfoResponse getRatePlanInfo(String ratePlan, String hotelId) {
    return ratePlansOutPort.getRatePlanInfo(ratePlan, hotelId);
  }

  @Override
  public List<PromotionCodeResponse> getPromotionCode(List<String> promotionCode, String hotelId) {
    if (promotionCode.isEmpty()) {
      throw new UnavailableRatesException(
          DIGITAL_NO_PROMO_CODE_EXCEPTION,
          "Promotion code list cannot be empty.");
    }
    return ratePlansOutPort.getPromotionCode(promotionCode, hotelId);
  }
}
