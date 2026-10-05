package uk.co.whitbread.promo.domain.model.promobatch.out;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoBatchResponse {

  private UUID batchId;
  private String campaignName;
  private String operaPromoCode;
  private Integer batchCount;
  private Integer codeLength;
  private String prefix;
  private PromoBatchStatus status;
  private String s3Key;
  private Boolean downloaded;
  private String password;
  private String notes;
  private String requestedBy;
  private LocalDate expiryDate;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private OffsetDateTime completedAt;
  private Boolean isMultiple;
  private Integer maxRedemptionLimit;
}
