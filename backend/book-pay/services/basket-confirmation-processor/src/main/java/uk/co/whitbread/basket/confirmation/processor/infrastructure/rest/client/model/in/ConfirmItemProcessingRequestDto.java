package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmItemProcessingRequestDto {

  private Integer status;
  private String reqAction;
  private String description;
  private List<String> errors;
  @JsonProperty("reported_at")
  private String reportedAt;
}
