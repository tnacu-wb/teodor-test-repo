package uk.co.whitbread.shared.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetRequest {
  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("client_id")
  private String clientId;
  @JsonProperty("result_url")
  private String resultUrl;
  @JsonProperty("mark_email_as_verified")
  private Boolean markEmailAsVerified;
  @JsonProperty("ttl_sec")
  private Integer ttlSeconds;
}