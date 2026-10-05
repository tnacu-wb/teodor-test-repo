package uk.co.whitbread.content.domain.model.seo.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.index.header.data.out.Icon;
import uk.co.whitbread.content.domain.model.index.header.data.out.MsIcon;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoResponse {

  private String pageTitle;
  private String pageDescription;
  private String cardImageUrl;
  private String faviconUrl;
  private List<Icon> icons;
  private List<MsIcon> msIcons;
  private Faq faq;
  private List<Hreflang> hreflangs;
}
