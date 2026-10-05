package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditBoxDto {

  private String icon;
  private String title;
  private String subtitle;

}
