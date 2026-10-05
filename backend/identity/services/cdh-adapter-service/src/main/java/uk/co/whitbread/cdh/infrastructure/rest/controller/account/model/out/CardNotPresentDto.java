package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardNotPresentDto {
  private String businessAccountPassword;
  private String businessAccountUsername;
}
