package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.PromoBoxResolutionResult;

@Component
public class PromoBoxResolver {

  public PromoBoxResolutionResult resolvePreEvaluation(
      PromoConfigRequest request) {

    if (!Boolean.TRUE.equals(request.getIsPromoBox())
        || Boolean.TRUE.equals(request.getIsAmendRequest())) {
      return PromoBoxResolutionResult.nonTerminal();
    }

    if (StringUtils.isBlank(request.getPromotionCode())) {
      return PromoBoxResolutionResult.terminal(PromoBoxStatus.EMPTY);
    }

    if (request.getPromoKind() == PromoKind.UNIQUE) {

      if (request.getUniquePromoCodeStatus() == null) {
        return PromoBoxResolutionResult.terminal(PromoBoxStatus.INVALID);
      }

      return switch (request.getUniquePromoCodeStatus()) {
        case REDEEMED -> PromoBoxResolutionResult.terminal(
            PromoBoxStatus.CODE_ALREADY_APPLIED);
        case EXPIRED -> PromoBoxResolutionResult.terminal(
            PromoBoxStatus.CODE_EXPIRED);
        default -> PromoBoxResolutionResult.nonTerminal();
      };
    }

    return PromoBoxResolutionResult.nonTerminal();
  }

  public PromoBoxResolutionResult resolvePostEvaluation(
      PromoConfigRequest request,
      PromotionsInformationResponse promoResponse) {

    if (!Boolean.TRUE.equals(request.getIsPromoBox())
        || Boolean.TRUE.equals(request.getIsAmendRequest())) {
      return PromoBoxResolutionResult.nonTerminal();
    }

    if (!Boolean.TRUE.equals(promoResponse.getShowPromo())
        && !Boolean.TRUE.equals(promoResponse.getIsWithinPromoWindow())) {
      return PromoBoxResolutionResult.terminal(PromoBoxStatus.INVALID);
    }

    boolean success =
        Boolean.TRUE.equals(promoResponse.getShowPromo())
            && Boolean.TRUE.equals(promoResponse.getIsWithinPromoWindow());

    return PromoBoxResolutionResult.terminal(
        success ? PromoBoxStatus.SUCCESS
            : PromoBoxStatus.UNAVAILABLE
    );
  }
}