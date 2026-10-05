package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class ConfirmItemProcessingRequestDto implements SelfValidation<ConfirmItemProcessingRequestDto> {
  private Integer status;
  private String reqAction;
  private String description;
  private List<String> errors;
  @JsonProperty("reported_at")
  private String reportedAt;

  public ConfirmItemProcessingRequestDto(Integer status, String reqAction, String description, List<String> errors,
      String reportedAt) {
    this.status = status;
    this.reqAction = reqAction;
    this.description = description;
    this.errors = errors;
    this.reportedAt = reportedAt;
    this.validateSelf();
  }
}
