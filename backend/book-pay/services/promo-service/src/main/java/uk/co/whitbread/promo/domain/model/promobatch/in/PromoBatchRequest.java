package uk.co.whitbread.promo.domain.model.promobatch.in;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoBatchRequest implements SelfValidation<PromoBatchRequest> {

  private String hotelId;
  private String operaPromoCode;
  private String prefix;
  private Integer batchCount;
  private Integer codeLength;
  private LocalDate expiryDate;
  private String notes;
  private String requestedBy;
  private String campaignName;
  private List<BatchEligibility> batchEligibilities;
  private Boolean isMultiple;
  private Integer maxRedemptionLimit;
}
