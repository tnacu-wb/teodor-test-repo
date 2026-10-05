package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusOptionsDto {

  private String activate;
  private String active;
  private String dispatching;
  private String cancelled;
  private String expired;

}
