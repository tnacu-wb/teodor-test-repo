package uk.co.whitbread.shared.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Auth0User {
  @JsonProperty("user_id")
  private String id;
  private String name;
  private String email;
  private String connection;
  private String password;
  @JsonProperty("email_verified")
  private Boolean emailVerified;
  @JsonProperty("app_metadata")
  private Map<String, Object> appMetadata;
  private List<Map<String, Object>> identities;
}