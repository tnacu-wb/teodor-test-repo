package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
public class BookingChannelInfoResponseDto {

  @JsonProperty("ratePlanSets")
  private List<String> ratePlanSets;

  @JsonProperty("sourceId")
  private String sourceId;

  @JsonProperty("generatedAt")
  private LocalDateTime generatedAt;
}
