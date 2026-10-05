package uk.co.whitbread.promo.infrastructure.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.infrastructure.repository.mapper.BatchEligibilityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchSummaryMapper;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchSortField;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.repository.projection.PromoBatchSummaryProjection;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoBatchStatusService {

  private final PromoBatchRepository promoBatchRepository;
  private final PromoBatchSummaryMapper promoBatchSummaryMapper;
  private final BatchEligibilityRepository batchEligibilityRepository;
  private final BatchEligibilityMapper batchEligibilityMapper;

  @Transactional
  public void setStatus(UUID batchId, PromoBatchStatus status) {
    promoBatchRepository
        .findById(batchId)
        .ifPresentOrElse(
            b -> {
              validateStateTransition(b.getStatus(), status, batchId);
              b.setStatus(status);
              b.setUpdatedAt(OffsetDateTime.now());
              promoBatchRepository.save(b);
              log.info("Batch {} status changed: {} -> {}", batchId, b.getStatus(), status);
            },
            () -> log.warn("Batch {} not found for status update", batchId));
  }

  @Transactional
  public boolean setStatusIfPending(UUID batchId, PromoBatchStatus newStatus) {
    return promoBatchRepository
        .findById(batchId)
        .map(
            b -> {
              if (b.getStatus() == PromoBatchStatus.PENDING) {
                validateStateTransition(b.getStatus(), newStatus, batchId);
                b.setStatus(newStatus);
                b.setUpdatedAt(OffsetDateTime.now());
                promoBatchRepository.save(b);
                log.info("Batch {} transitioned: PENDING -> {}", batchId, newStatus);
                return true;
              } else {
                log.warn(
                    "Batch {} was not in PENDING state — current state: {}", batchId,
                    b.getStatus());
                return false;
              }
            })
        .orElseGet(
            () -> {
              log.warn("Batch {} not found", batchId);
              return false;
            });
  }

  @Transactional
  public void markCompleted(UUID batchId) {
    promoBatchRepository
        .findById(batchId)
        .ifPresentOrElse(
            b -> {
              validateStateTransition(b.getStatus(), PromoBatchStatus.COMPLETED, batchId);
              b.setStatus(PromoBatchStatus.COMPLETED);
              b.setCompletedAt(OffsetDateTime.now());
              b.setUpdatedAt(OffsetDateTime.now());
              promoBatchRepository.save(b);
              log.info("Batch {} marked as COMPLETED", batchId);
            },
            () -> log.warn("Batch {} not found for completion marking", batchId));
  }

  @Transactional
  public void markFailed(UUID batchId) {
    promoBatchRepository
        .findById(batchId)
        .ifPresentOrElse(
            b -> {
              validateStateTransition(b.getStatus(), PromoBatchStatus.FAILED, batchId);
              b.setStatus(PromoBatchStatus.FAILED);
              b.setUpdatedAt(OffsetDateTime.now());
              promoBatchRepository.save(b);
              log.error("Batch {} marked as FAILED", batchId);
            },
            () -> log.warn("Batch {} not found for failure marking", batchId));
  }

  private void validateStateTransition(
      PromoBatchStatus currentStatus, PromoBatchStatus newStatus, UUID batchId)
      throws IllegalStateException {

    if (currentStatus == newStatus) {
      log.warn(
          "Batch {} already in state {} — skipping redundant transition", batchId, currentStatus);
      return;
    }

    boolean validTransition = isValidTransition(currentStatus, newStatus);
    if (!validTransition) {
      String msg =
          String.format(
              "Invalid state transition for batch %s: %s -> %s",
              batchId, currentStatus, newStatus);
      log.error(msg);
      throw new IllegalStateException(msg);
    }
  }

  private static boolean isValidTransition(PromoBatchStatus currentStatus,
      PromoBatchStatus newStatus) {
    return switch (currentStatus) {
      case PENDING -> newStatus == PromoBatchStatus.RUNNING
          || newStatus == PromoBatchStatus.FAILED
          || newStatus == PromoBatchStatus.COMPLETED;
      case RUNNING -> newStatus == PromoBatchStatus.COMPLETED
          || newStatus == PromoBatchStatus.FAILED;
      case COMPLETED -> newStatus == PromoBatchStatus.EXPIRED;
      case FAILED -> newStatus == PromoBatchStatus.PENDING
          || newStatus == PromoBatchStatus.RUNNING;
      case EXPIRED -> false;
    };
  }

  public PromoBatchSummaryPage getPromoBatchSummary(Pageable pageable) {
    Pageable safePageable = sanitizePageable(pageable);
    var page = promoBatchRepository.findAllProjectedBy(safePageable);
    List<UUID> batchIds = page.getContent().stream()
            .map(PromoBatchSummaryProjection::getBatchId)
            .toList();

    List<BatchEligibilityEntity> eligibilityEntities = batchIds.isEmpty()
            ? List.of()
            : batchEligibilityRepository.findByBatchIdIn(batchIds);

    Map<UUID, List<BatchEligibilityEntity>> eligibilityMap =
            eligibilityEntities.stream()
                    .collect(Collectors.groupingBy(
                            BatchEligibilityEntity::getBatchId));

    List<PromoBatchSummary> summaries =
            page.getContent().stream()
                 .map(projection -> {
                   PromoBatchSummary summary =
                           promoBatchSummaryMapper.toSummaryModel(projection);

                   summary.setBatchEligibilities(
                           batchEligibilityMapper.toSummaryModel(
                                   eligibilityMap.getOrDefault(
                                           projection.getBatchId(),
                                           List.of())));

                   return summary;
                 }).toList();

    return PromoBatchSummaryPage.builder()
        .promoBatchSummary(summaries)
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNumber(page.getNumber())
        .pageSize(page.getSize())
        .pageable(page.getPageable())
        .build();
  }

  private Pageable sanitizePageable(Pageable pageable) {
    Sort safeSort = Sort.by(
        pageable.getSort().stream()
            .map(this::sanitizeOrder)
            .toList()
    );

    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
  }

  private Sort.Order sanitizeOrder(Sort.Order order) {
    return PromoBatchSortField.isValid(order.getProperty())
        ? order
        : new Sort.Order(order.getDirection(), PromoBatchSortField.UPDATED_AT.name());
  }
}
