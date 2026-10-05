package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoError {
  @JsonProperty("Code")
  private String code;

  @JsonProperty("Target")
  private String target;

  @JsonProperty("Message")
  private String message;
}
