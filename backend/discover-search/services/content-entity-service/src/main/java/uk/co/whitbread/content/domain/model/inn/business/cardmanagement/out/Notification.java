package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  private String error;
  private String info;
  private String question;
  private String success;
  private String alert;

}
