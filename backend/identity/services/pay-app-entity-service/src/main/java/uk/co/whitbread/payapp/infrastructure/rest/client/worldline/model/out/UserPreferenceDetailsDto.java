package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

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
public class UserPreferenceDetailsDto {

  @JsonProperty("showSMSStopsToCardholder")
  private Boolean showSmsStopsToCardholder;
  private Boolean sendCardsToCardholder;
  private String accountName;
  private Integer roleId;
  private Integer accountNumber;
  private String roleDescription;
}