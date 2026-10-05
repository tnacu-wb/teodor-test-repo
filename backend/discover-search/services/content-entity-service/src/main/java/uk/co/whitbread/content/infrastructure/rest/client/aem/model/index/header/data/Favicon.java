package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.index.header.data.out.Icon;
import uk.co.whitbread.content.domain.model.index.header.data.out.MsIcon;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Favicon {

  private String faviconUrl;
  private List<Icon> icons;
  private List<MsIcon> msIcons;
}
