package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAccountLink {

  private String title;
  private List<SubMenuLink> subMenuLinks;

}
