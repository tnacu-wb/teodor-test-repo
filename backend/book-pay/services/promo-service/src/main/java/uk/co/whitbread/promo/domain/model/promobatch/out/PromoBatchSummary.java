package uk.co.whitbread.promo.domain.model.promobatch.out;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoBatchSummary {

  private UUID batchId;
  private String campaignName;
  private String operaPromoCode;
  private String prefix;
  private Integer batchCount;
  private PromoBatchStatus status;
  private String s3Key;
  private String notes;
  private Boolean downloaded;
  private String password;
  private String requestedBy;
  private OffsetDateTime createdAt;
  private List<BatchEligibilitySummary> batchEligibilities;
  private Boolean isMultiple;
  private Integer maxRedemptionLimit;
}
