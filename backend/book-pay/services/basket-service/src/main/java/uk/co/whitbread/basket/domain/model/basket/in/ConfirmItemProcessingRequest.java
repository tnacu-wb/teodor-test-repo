package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class ConfirmItemProcessingRequest implements SelfValidation<ConfirmItemProcessingRequest> {
  private Integer status;
  private String reqAction;
  private String description;
  private List<String> errors;
  private String reportedAt;

  public ConfirmItemProcessingRequest(Integer status, String reqAction, String description, List<String> errors,
      String reportedAt) {
    this.status = status;
    this.reqAction = reqAction;
    this.description = description;
    this.errors = errors;
    this.reportedAt = reportedAt;
    this.validateSelf();
  }
}
