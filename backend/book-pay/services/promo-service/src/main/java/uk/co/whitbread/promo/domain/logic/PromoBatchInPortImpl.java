package uk.co.whitbread.promo.domain.logic;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKindResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemPromoCodeResponse;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchRepositoryOutPort;

@Service
@RequiredArgsConstructor
public class PromoBatchInPortImpl implements PromoBatchInPort {

  private final PromoBatchRepositoryOutPort promoBatchRepositoryOutPort;

  @Override
  public PromoBatchResponse createPromoBatch(PromoBatchRequest promoBatchRequest) {
    return promoBatchRepositoryOutPort.createPromoBatch(promoBatchRequest);
  }

  @Override
  public PromoBatchSummary getPromoBatchById(UUID batchId) {
    return promoBatchRepositoryOutPort.getPromoBatchById(batchId);
  }

  @Override
  public PromoBatchSummaryPage getPromoBatchSummary(Pageable pageable) {
    return promoBatchRepositoryOutPort.getPromoBatchSummary(pageable);
  }

  @Override
  public void recoverInterruptedBatches() {
    promoBatchRepositoryOutPort.recoverInterruptedBatches();
  }

  @Override
  public void markAsDownloaded(UUID batchId) {
    promoBatchRepositoryOutPort.markAsDownloaded(batchId);
  }

  @Override
  public PromoKindResponse validatePromoKind(PromoKindRequest promoKindRequest) {
    return promoBatchRepositoryOutPort.validatePromoKind(promoKindRequest);
  }

  @Override
  public RedeemPromoCodeResponse redeemPromoCode(RedeemPromoCodeRequest redeemPromoCodeRequest) {
    return promoBatchRepositoryOutPort.redeemPromoCode(redeemPromoCodeRequest);
  }

  @Override
  public void updatePromoBatchStatusToExpiry() {
    promoBatchRepositoryOutPort.updatePromoBatchStatusToExpiry();
  }

  @Override
  public void deleteExpiredPromoCodesAfterRetention() {
    promoBatchRepositoryOutPort.deleteExpiredPromoCodesAfterRetention();
  }
}
