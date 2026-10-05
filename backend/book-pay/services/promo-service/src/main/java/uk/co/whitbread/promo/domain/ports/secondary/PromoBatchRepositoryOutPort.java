package uk.co.whitbread.promo.domain.ports.secondary;


import java.util.UUID;
import org.springframework.data.domain.Pageable;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKindResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemPromoCodeResponse;

public interface PromoBatchRepositoryOutPort {

  PromoBatchResponse createPromoBatch(PromoBatchRequest promoBatchRequest);

  PromoBatchSummary getPromoBatchById(UUID batchId);

  PromoBatchSummaryPage getPromoBatchSummary(Pageable pageable);

  void recoverInterruptedBatches();

  void markAsDownloaded(UUID batchId);

  PromoKindResponse validatePromoKind(PromoKindRequest promoKindRequest);

  RedeemPromoCodeResponse redeemPromoCode(RedeemPromoCodeRequest redeemPromoCodeRequest);

  void updatePromoBatchStatusToExpiry();

  void deleteExpiredPromoCodesAfterRetention();
}
