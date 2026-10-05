package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardNotPresent {
  @JsonProperty("BusinessAccountPassword")
  private String businessAccountPassword;
  @JsonProperty("BusinessAccountUsername")
  private String businessAccountUsername;
}
