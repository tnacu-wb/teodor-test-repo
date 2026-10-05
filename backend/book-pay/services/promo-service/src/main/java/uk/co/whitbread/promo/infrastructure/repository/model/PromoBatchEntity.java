package uk.co.whitbread.promo.infrastructure.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "promotions", name = "promo_batch")
public class PromoBatchEntity {

  @Id
  @Column(name = "batch_id", updatable = false, nullable = false)
  private UUID batchId;

  @Column(name = "campaign_name")
  private String campaignName;

  @Column(name = "opera_promo", length = 32)
  @Size(max = 32)
  private String operaPromoCode;

  @Column(name = "batch_count", nullable = false)
  @Min(0)
  @Max(1_000_000)
  private Integer batchCount;

  @Column(name = "code_length", nullable = false)
  @Min(8)
  @Max(32)
  private Integer codeLength;

  @Column(name = "prefix", length = 20)
  @Size(max = 20)
  private String prefix;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private PromoBatchStatus status;

  @Column(name = "s3_key", length = 256)
  @Size(max = 256)
  private String s3Key;

  @Column(name = "downloaded")
  private Boolean downloaded;

  @Column(name = "password", length = 20)
  @Size(max = 20)
  private String password;

  @Column(name = "notes", columnDefinition = "text")
  private String notes;

  @Column(name = "requested_by", length = 128)
  @Size(max = 128)
  private String requestedBy;

  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  @Column(name = "created_at", updatable = false, nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @Column(name = "completed_at")
  private OffsetDateTime completedAt;

  @Column(name = "is_multiple", nullable = false)
  private Boolean isMultiple;

  @Column(name = "max_redemption_limit")
  private Integer maxRedemptionLimit;

  @PrePersist
  public void prePersist() {
    if (this.batchId == null) {
      this.batchId = UUID.randomUUID();
    }
    OffsetDateTime now = OffsetDateTime.now();
    if (this.createdAt == null) {
      this.createdAt = now;
    }
    if (this.updatedAt == null) {
      this.updatedAt = now;
    }
    if (this.downloaded == null) {
      this.downloaded = false;
    }
    if (this.isMultiple == null) {
      this.isMultiple = false;
    }
  }

}