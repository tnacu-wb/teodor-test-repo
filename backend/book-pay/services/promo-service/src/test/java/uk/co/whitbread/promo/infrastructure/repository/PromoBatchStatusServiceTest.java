package uk.co.whitbread.promo.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;
import uk.co.whitbread.promo.domain.model.promobatch.out.BatchEligibilitySummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchSummaryMapper;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.repository.projection.PromoBatchSummaryProjection;
import uk.co.whitbread.promo.infrastructure.repository.mapper.BatchEligibilityMapper;

@ExtendWith(MockitoExtension.class)
class PromoBatchStatusServiceTest {

    @Mock
    private PromoBatchRepository promoBatchRepository;

    @Mock
    private PromoBatchSummaryMapper promoBatchSummaryMapper;

    @InjectMocks
    private PromoBatchStatusService service;

    @Mock
    private BatchEligibilityRepository batchEligibilityRepository;

    @Mock
    private BatchEligibilityMapper batchEligibilityMapper;

    @Test
    void setStatus_validTransition_updatesStatus() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.PENDING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        service.setStatus(id, PromoBatchStatus.RUNNING);

        assertEquals(PromoBatchStatus.RUNNING, batch.getStatus());
        verify(promoBatchRepository).save(batch);
    }

    @Test
    void setStatus_invalidTransition_throwsException() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.COMPLETED);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        assertThrows(
                IllegalStateException.class,
                () -> service.setStatus(id, PromoBatchStatus.RUNNING));
    }

    @Test
    void setStatus_batchNotFound_doesNothing() {
        UUID id = UUID.randomUUID();
        when(promoBatchRepository.findById(id)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.setStatus(id, PromoBatchStatus.RUNNING));
        verify(promoBatchRepository, never()).save(any());
    }

    @Test
    void setStatusIfPending_pendingBatch_updatesAndReturnsTrue() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.PENDING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        boolean result = service.setStatusIfPending(id, PromoBatchStatus.RUNNING);

        assertTrue(result);
        assertEquals(PromoBatchStatus.RUNNING, batch.getStatus());
        verify(promoBatchRepository).save(batch);
    }

    @Test
    void setStatusIfPending_notPending_returnsFalse() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.RUNNING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        boolean result = service.setStatusIfPending(id, PromoBatchStatus.COMPLETED);

        assertFalse(result);
        verify(promoBatchRepository, never()).save(any());
    }

    @Test
    void setStatusIfPending_batchNotFound_returnsFalse() {
        when(promoBatchRepository.findById(any())).thenReturn(Optional.empty());

        boolean result =
                service.setStatusIfPending(UUID.randomUUID(), PromoBatchStatus.RUNNING);

        assertFalse(result);
    }

    @Test
    void markCompleted_validTransition_setsCompleted() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.RUNNING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        service.markCompleted(id);

        assertEquals(PromoBatchStatus.COMPLETED, batch.getStatus());
        verify(promoBatchRepository).save(batch);
    }

    @Test
    void markFailed_validTransition_setsFailed() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.RUNNING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        service.markFailed(id);

        assertEquals(PromoBatchStatus.FAILED, batch.getStatus());
        verify(promoBatchRepository).save(batch);
    }

    @Test
    void getPromoBatchSummary_invalidSortField_isSanitizedAndReturnsPage() {
        Pageable pageable =
                PageRequest.of(0, 5, Sort.by(Sort.Order.asc("invalid_field")));

        PromoBatchSummaryProjection projection = mock(PromoBatchSummaryProjection.class);
        Page<PromoBatchSummaryProjection> page =
                new PageImpl<>(List.of(projection), pageable, 1);

        when(promoBatchRepository.findAllProjectedBy(any()))
                .thenReturn(page);

        when(promoBatchSummaryMapper.toSummaryModel(any()))
                .thenReturn(mock(PromoBatchSummary.class));

        PromoBatchSummaryPage result = service.getPromoBatchSummary(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getPromoBatchSummary().size());
    }

    @Test
    void setStatus_sameStatus_stillSavesWithoutException() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.RUNNING);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        service.setStatus(id, PromoBatchStatus.RUNNING);

        verify(promoBatchRepository).save(batch);
    }

    @Test
    void setStatus_failedToPending_validTransition() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.FAILED);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        service.setStatus(id, PromoBatchStatus.PENDING);

        assertEquals(PromoBatchStatus.PENDING, batch.getStatus());
        verify(promoBatchRepository).save(batch);
    }

    @Test
    void setStatus_expiredToRunning_invalidTransition() {
        UUID id = UUID.randomUUID();
        PromoBatchEntity batch = new PromoBatchEntity();
        batch.setStatus(PromoBatchStatus.EXPIRED);

        when(promoBatchRepository.findById(id)).thenReturn(Optional.of(batch));

        assertThrows(
                IllegalStateException.class,
                () -> service.setStatus(id, PromoBatchStatus.RUNNING)
        );

        verify(promoBatchRepository, never()).save(any());
    }

    @Test
    void getPromoBatchSummary_shouldPopulateBatchEligibilities() {

        // Arrange
        UUID batchId = UUID.randomUUID();

        Pageable pageable = PageRequest.of(0, 5);

        PromoBatchSummaryProjection projection =
                mock(PromoBatchSummaryProjection.class);

        when(projection.getBatchId()).thenReturn(batchId);

        Page<PromoBatchSummaryProjection> page =
                new PageImpl<>(List.of(projection), pageable, 1);

        when(promoBatchRepository.findAllProjectedBy(any()))
                .thenReturn(page);

        PromoBatchSummary summary = PromoBatchSummary.builder().build();

        when(promoBatchSummaryMapper.toSummaryModel(projection))
                .thenReturn(summary);

        BatchEligibilityEntity entity = BatchEligibilityEntity.builder()
                .batchId(batchId)
                .region(Region.GB)
                .channel(Channel.PI)
                .platform(Platform.WEB)
                .build();

        when(batchEligibilityRepository.findByBatchIdIn(List.of(batchId)))
                .thenReturn(List.of(entity));

        BatchEligibilitySummary eligibility =
                BatchEligibilitySummary.builder()
                        .region(Region.GB)
                        .channel(Channel.PI)
                        .platforms(List.of(Platform.WEB))
                        .build();

        when(batchEligibilityMapper.toSummaryModel(anyList()))
                .thenReturn(List.of(eligibility));

        // Act
        PromoBatchSummaryPage result =
                service.getPromoBatchSummary(pageable);

        // Assert
        assertEquals(1, result.getPromoBatchSummary().size());

        PromoBatchSummary returned =
                result.getPromoBatchSummary().get(0);

        assertNotNull(returned.getBatchEligibilities());
        assertEquals(1, returned.getBatchEligibilities().size());

        verify(batchEligibilityRepository)
                .findByBatchIdIn(List.of(batchId));

        verify(batchEligibilityMapper)
                .toSummaryModel(anyList());
    }
}
