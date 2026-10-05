package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDto {

  private FaqDto faq;
  private String needHelp;
  private TourDto tour;
  private ContactDto contact;

}
