package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
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
