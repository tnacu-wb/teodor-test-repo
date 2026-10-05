package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class MaxRoomOccuRuleResp extends ModelValidator<MaxRoomOccuRuleResp> {

  @NotEmpty
  String channelId;
  @NotEmpty
  String brand;
  @NotNull
  List<MaxRoomOccupancyData> maxOccupancyData;
  @NotNull
  LocalDateTime generatedAt;

  public MaxRoomOccuRuleResp(String channelId,
      String brand,
      List<MaxRoomOccupancyData> maxOccupancyData,
      LocalDateTime genAt) {
    this.channelId = channelId;
    this.brand = brand;
    this.maxOccupancyData = maxOccupancyData;
    this.generatedAt = genAt;
    this.validateSelf();
  }
}
