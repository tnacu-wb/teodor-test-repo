package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PromoBatchResponseDto {

  private UUID batchId;
  private String campaignName;
  private String operaPromoCode;
  private String prefix;
  private Integer batchCount;
  private Integer codeLength;
  private PromoBatchStatus status;
  private String s3Key;
  private String notes;
  private Boolean downloaded;
  private String password;
  private String requestedBy;
  private LocalDate expiryDate;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private OffsetDateTime completedAt;
  private Boolean isMultiple;
  private Integer maxRedemptionLimit;
}
