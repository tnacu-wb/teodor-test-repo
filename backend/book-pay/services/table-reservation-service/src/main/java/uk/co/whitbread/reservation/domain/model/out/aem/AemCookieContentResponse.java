package uk.co.whitbread.reservation.domain.model.out.aem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AemCookieContentResponse {
  @JsonProperty("cookiePolicies")
  private CookiePolicies cookiePolicies;

}
