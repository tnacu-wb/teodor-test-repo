package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedPartnerCredentialsDto {

  @JsonProperty("Username")
  private String username;

  @JsonProperty("Password")
  private String password;

}
