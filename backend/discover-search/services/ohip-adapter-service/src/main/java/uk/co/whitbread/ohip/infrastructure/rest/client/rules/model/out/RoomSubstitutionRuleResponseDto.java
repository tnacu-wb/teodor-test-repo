package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
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
public class RoomSubstitutionRuleResponseDto {

  @JsonProperty("requestDetails")
  private RoomSubstitutionRequestDetailsDto requestDetails;

  @JsonProperty("generatedAt")
  private Date generatedAt;

  @JsonProperty("substitution-list")
  private List<RoomSubstitutionDto> substitutionList;

}
