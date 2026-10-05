package uk.co.whitbread.promo.infrastructure.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityId;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;

@Repository
public interface BatchEligibilityRepository extends JpaRepository<BatchEligibilityEntity, BatchEligibilityId> {

  List<BatchEligibilityEntity> findByBatchIdIn(List<UUID> batchIds);

  @Query(
      """
    SELECT b
    FROM PromoBatchEntity b
    WHERE b.prefix = :promoCode
      """
  )
  List<PromoBatchEntity> findByMatchingPrefix(@Param("promoCode") String promoCode);

  boolean existsByBatchIdAndRegionAndChannelAndPlatform(
          UUID batchId,
          Region region,
          Channel channel,
          Platform platform);
}
