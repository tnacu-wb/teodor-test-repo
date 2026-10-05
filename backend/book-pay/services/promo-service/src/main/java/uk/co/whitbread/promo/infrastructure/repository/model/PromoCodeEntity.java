package uk.co.whitbread.promo.infrastructure.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "promotions", name = "promo_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeEntity {

  @Id
  @Column(name = "code", nullable = false, updatable = false, length = 32)
  private String code;

  @Column(name = "batch_id", length = 20)
  private UUID batchId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private PromoCodeStatus status;

  @Column(name = "booking_reference", length = 64)
  private String bookingReference;

  @Column(name = "redeemed_at")
  private OffsetDateTime redeemedAt;

  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}