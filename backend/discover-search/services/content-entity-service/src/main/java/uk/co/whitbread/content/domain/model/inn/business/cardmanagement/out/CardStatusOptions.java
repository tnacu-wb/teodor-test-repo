package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatusOptions {

  private String activate;
  private String active;
  private String dispatching;
  private String cancelled;
  private String expired;

}
