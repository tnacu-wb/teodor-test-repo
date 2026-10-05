package uk.co.whitbread.promo.infrastructure.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;

@ExtendWith(MockitoExtension.class)
class PromoBatchExpiryAndCleanUpSchedulerTest {

    @InjectMocks
    private PromoBatchExpiryAndCleanUpScheduler promoBatchExpiryAndCleanUpScheduler;

    @Mock
    private PromoBatchInPort promoBatchInPort;

    @Test
    void test_updatePromoBatchStatusExpiryAndCleanUpJob() {

        // Act
        promoBatchExpiryAndCleanUpScheduler.updatePromoBatchStatusExpiryAndCleanUpJob();

        // Assert
        verify(promoBatchInPort, times(1)).updatePromoBatchStatusToExpiry();
        verify(promoBatchInPort, times(1)).deleteExpiredPromoCodesAfterRetention();
    }
}
