package uk.co.whitbread.spending.domain.model.in.worldline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedPartnerCredentials {
  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;
}
