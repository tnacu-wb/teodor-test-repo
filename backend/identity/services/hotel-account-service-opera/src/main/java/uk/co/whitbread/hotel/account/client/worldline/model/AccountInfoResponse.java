package uk.co.whitbread.hotel.account.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponse {

  @JsonProperty("responseCode")
  private String responseCode;

  @JsonProperty("data")
  private AccountInfo data;

  @JsonProperty("errors")
  private List<AccountInfoError> errors;
}
