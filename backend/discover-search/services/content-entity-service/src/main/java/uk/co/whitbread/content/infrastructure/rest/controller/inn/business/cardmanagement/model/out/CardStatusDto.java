package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusDto {

  @JsonProperty("options")
  private CardStatusOptionsDto cardStatusOptions;

}
