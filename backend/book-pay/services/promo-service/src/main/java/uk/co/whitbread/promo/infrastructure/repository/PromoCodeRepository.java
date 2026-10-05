package uk.co.whitbread.promo.infrastructure.repository;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

import jakarta.persistence.QueryHint;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

public interface PromoCodeRepository extends JpaRepository<PromoCodeEntity, String> {

  @Query("SELECT COUNT(p) FROM PromoCodeEntity p WHERE p.batchId = :batchId")
  long countByBatchId(@Param("batchId") UUID batchId);


  List<PromoCodeEntity> findByBatchId(UUID batchId);

  @Modifying
  @Query(
      value = """
          UPDATE promotions.promo_code pc
          SET status = 'REDEEMED',
              booking_reference = :bookingReference,
              redeemed_at = :redeemedAt
          FROM promotions.promo_batch pb
          WHERE pc.code = :promoCode
            AND pc.batch_id = pb.batch_id
            AND pc.status = 'ISSUED'
            AND pb.expiry_date >= CURRENT_DATE
          """,
      nativeQuery = true
  )
  int redeemPromoCode(
      @Param("promoCode") String promoCode,
      @Param("bookingReference") String bookingReference,
      @Param("redeemedAt") OffsetDateTime redeemedAt
  );

  @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "1000"))
  @Query(""" 
          SELECT p FROM PromoCodeEntity p WHERE p.batchId = :batchId 
          """)
  Stream<PromoCodeEntity> streamByBatchId(@Param("batchId") UUID batchId);

  @Modifying
  @Transactional
  @Query("DELETE FROM PromoCodeEntity p WHERE p.batchId = :batchId")
  void deleteByBatchId(UUID batchId);

  @Modifying
  @Query(
          value = """
        DELETE FROM promotions.promo_code pc
        USING promotions.promo_batch pb
        WHERE pc.batch_id = pb.batch_id
          AND pb.status = 'EXPIRED'
          AND pb.expiry_date < CURRENT_DATE - INTERVAL '60 days'
        """,
          nativeQuery = true
  )
  int deleteExpiredPromoCodesAfterRetention();
}
