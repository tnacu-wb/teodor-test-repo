package uk.co.whitbread.promo.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchStatus;
import uk.co.whitbread.promo.infrastructure.repository.projection.PromoBatchSummaryProjection;

@Repository
public interface PromoBatchRepository extends JpaRepository<PromoBatchEntity, UUID> {

  @Query("""
      SELECT
        b.batchId AS batchId,
        b.campaignName AS campaignName,
        b.operaPromoCode AS operaPromoCode,
        b.prefix AS prefix,
        b.batchCount AS batchCount,
        b.status AS status,
        b.s3Key AS s3Key,
        b.notes AS notes,
        b.downloaded AS downloaded,
        b.password AS password,
        b.requestedBy AS requestedBy,
        b.createdAt AS createdAt,
        b.isMultiple AS isMultiple,
        b.maxRedemptionLimit AS maxRedemptionLimit
      FROM PromoBatchEntity b
      """)
  Page<PromoBatchSummaryProjection> findAllProjectedBy(Pageable pageable);

  List<PromoBatchEntity> findByStatusIn(List<PromoBatchStatus> statuses);

  @Modifying
  @Query("""
          UPDATE PromoBatchEntity b
          SET b.downloaded = true
          WHERE b.id = :batchId
            and b.downloaded = false
      """)
  int markAsDownloaded(@Param("batchId") UUID batchId);

  @Modifying
  @Transactional
  @Query("""
          UPDATE PromoBatchEntity b
          SET b.s3Key = :s3Key
          WHERE b.id = :batchId
      """)
  int updateS3Key(
      @Param("batchId") UUID batchId,
      @Param("s3Key") String s3Key
  );

  List<PromoBatchEntity> findAllByOperaPromoCode(String promoCode);

  @Modifying
  @Query("""
       UPDATE PromoBatchEntity p
       SET p.status = 'EXPIRED'
       WHERE p.expiryDate < :today
       AND p.status = 'COMPLETED'
       """)
  int expireOldBatches(@Param("today") LocalDate today);

  @Modifying
  @Query("""
    UPDATE PromoBatchEntity b
    SET b.maxRedemptionLimit = b.maxRedemptionLimit - 1
    WHERE b.batchId = :batchId
      AND b.maxRedemptionLimit > 0
      """)
   int decrementMaxRedemptionLimit(UUID batchId);

  @Query("""
    SELECT COUNT(b) > 0
    FROM PromoBatchEntity b
    WHERE b.prefix = :prefix
      AND b.isMultiple = true
      AND b.status IN :statuses
      AND b.expiryDate >= CURRENT_DATE
      """)
   boolean existsActiveIsMultipleBatch(
          @Param("prefix") String prefix,
          @Param("statuses") List<PromoBatchStatus> statuses);
}
