package uk.co.whitbread.content.domain.model.inn.business.header.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Help {

  private Faq faq;
  private String needHelp;
  private Tour tour;
  private Contact contact;

}
