package uk.co.whitbread.shared.cdh.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCustomerAccountResponse {

  @JsonProperty("RequestedDateTime")
  private String requestedDateTime;

  @JsonProperty("RequestId")
  private String requestId;

  @JsonProperty("CustomerAccountId")
  private String customerAccountId;

  @JsonProperty("Deleted")
  private boolean deleted;

}
