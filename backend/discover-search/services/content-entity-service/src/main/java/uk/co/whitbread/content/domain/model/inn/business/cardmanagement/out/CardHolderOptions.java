package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHolderOptions {

  private String resendCode;
  private String registered;

}
