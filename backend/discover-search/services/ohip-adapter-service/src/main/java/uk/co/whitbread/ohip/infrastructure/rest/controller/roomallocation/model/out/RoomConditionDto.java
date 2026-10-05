package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomConditionDto {

  @JsonProperty("roomCondition")
  private RoomConditionValueDto roomConditionValue;

}
