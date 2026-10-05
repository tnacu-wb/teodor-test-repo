package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomSubstitutionRequestDetailsDto {

  @JsonProperty("adults")
  private Integer adults;

  @JsonProperty("children")
  private Integer children;

  @JsonProperty("roomType")
  private String roomType;

  @JsonProperty("cotRequired")
  private Boolean cotRequired;

  @JsonProperty("pms")
  private String pms;

}
