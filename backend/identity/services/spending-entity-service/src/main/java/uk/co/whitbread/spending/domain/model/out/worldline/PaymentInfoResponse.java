package uk.co.whitbread.spending.domain.model.out.worldline;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentInfoResponse {
  @JsonProperty("responseCode")
  private String responseCode;

  @JsonProperty("data")
  private List<PaymentData> data;

  @JsonProperty("errors")
  private String errors;
}
