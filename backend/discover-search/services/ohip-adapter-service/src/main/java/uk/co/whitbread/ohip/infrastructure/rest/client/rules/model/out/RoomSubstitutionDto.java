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
public class RoomSubstitutionDto {

  @JsonProperty("type")
  private String type;

  @JsonProperty("silent")
  private Boolean silent;

  @JsonProperty("specialRequest")
  private String specialRequest;

  @JsonProperty("accessibleSpecialRequest")
  private String accessibleSpecialRequest;

  @JsonProperty("codePackage")
  private String codePackage;

}
