package uk.co.whitbread.promo.domain.logic;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.RedeemPromoCodeRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemStatus;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemPromoCodeResponse;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKindResponse;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchRepositoryOutPort;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PromoBatchInPortImplTest {

    @InjectMocks
    private PromoBatchInPortImpl promoBatchInPort;

    @Mock
    private PromoBatchRepositoryOutPort promoBatchRepositoryOutPort;

    @Test
    void createPromoBatch_shouldDelegateToRepository() {
        PromoBatchRequest request = PromoBatchRequest.builder().build();
        PromoBatchResponse response = PromoBatchResponse.builder().build();

        when(promoBatchRepositoryOutPort.createPromoBatch(request))
                .thenReturn(response);

        PromoBatchResponse result = promoBatchInPort.createPromoBatch(request);

        assertSame(response, result);
        verify(promoBatchRepositoryOutPort).createPromoBatch(request);
    }

    @Test
    void getPromoBatchById_shouldDelegateToRepository() {
        UUID batchId = UUID.randomUUID();
        PromoBatchSummary summary = PromoBatchSummary.builder().build();

        when(promoBatchRepositoryOutPort.getPromoBatchById(batchId))
                .thenReturn(summary);

        PromoBatchSummary result = promoBatchInPort.getPromoBatchById(batchId);

        assertSame(summary, result);
        verify(promoBatchRepositoryOutPort).getPromoBatchById(batchId);
    }

    @Test
    void getPromoBatchSummary_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        PromoBatchSummaryPage page = PromoBatchSummaryPage.builder().build();

        when(promoBatchRepositoryOutPort.getPromoBatchSummary(pageable))
                .thenReturn(page);

        PromoBatchSummaryPage result = promoBatchInPort.getPromoBatchSummary(pageable);

        assertSame(page, result);
        verify(promoBatchRepositoryOutPort).getPromoBatchSummary(pageable);
    }

    @Test
    void recoverInterruptedBatches_shouldDelegateToRepository() {
        promoBatchInPort.recoverInterruptedBatches();

        verify(promoBatchRepositoryOutPort).recoverInterruptedBatches();
    }

    @Test
    void markAsDownloaded_shouldDelegateToRepository() {
        UUID batchId = UUID.randomUUID();

        promoBatchInPort.markAsDownloaded(batchId);

        verify(promoBatchRepositoryOutPort).markAsDownloaded(batchId);
    }

    @Test
    void validatePromoKind_shouldDelegateToRepository() {
        PromoKindRequest request = PromoKindRequest.builder()
                .promoCode("PROMO123")
                .country("GB")
                .channel("PI")
                .subChannel("WEB")
                .build();
        PromoKindResponse response = PromoKindResponse.builder().build();

        when(promoBatchRepositoryOutPort.validatePromoKind(request))
                .thenReturn(response);

        PromoKindResponse result = promoBatchInPort.validatePromoKind(request);

        assertSame(response, result);
        verify(promoBatchRepositoryOutPort).validatePromoKind(request);
    }

    @Test
    void redeemPromoCode_shouldDelegateToRepository() {

        // Arrange
        RedeemPromoCodeRequest request = RedeemPromoCodeRequest.builder()
                .promoCode("PROMO123")
                .bookingReference("BOOK123")
                .build();

        RedeemPromoCodeResponse response = RedeemPromoCodeResponse.builder()
                .status(RedeemStatus.REDEEMED)
                .message("Promo code redeemed successfully")
                .build();

        when(promoBatchRepositoryOutPort.redeemPromoCode(request))
                .thenReturn(response);

        // Act
        RedeemPromoCodeResponse result =
                promoBatchInPort.redeemPromoCode(request);

        // Assert
        assertSame(response, result);
        verify(promoBatchRepositoryOutPort)
                .redeemPromoCode(request);
    }

    @Test
    void updatePromoBatchStatusToExpiry_shouldDelegateToRepository() {

        // Act
        promoBatchInPort.updatePromoBatchStatusToExpiry();

        // Assert
        verify(promoBatchRepositoryOutPort)
                .updatePromoBatchStatusToExpiry();
    }

    @Test
    void deleteExpiredPromoCodesAfterRetention_shouldDelegateToRepository() {

        // Act
        promoBatchInPort.deleteExpiredPromoCodesAfterRetention();

        // Assert
        verify(promoBatchRepositoryOutPort)
                .deleteExpiredPromoCodesAfterRetention();
    }
}
