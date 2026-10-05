package uk.co.whitbread.ohip.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;

public interface RatePlansOutPort {

  RatePlansResponse getRatePlans(List<String> ratePlans, String hotelId);

  NegotiatedRatesResponse getNegotiatedRatesForProfileId(String profileId);

  RatePlanInfoResponse getRatePlanInfo(String ratePlan, String hotelId);

  List<PromotionCodeResponse> getPromotionCode(List<String> promotionCode, String hotelId);
}
