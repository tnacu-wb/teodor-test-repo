package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardsDto {

  private String title;
  private String link;
  private String icon;
}
