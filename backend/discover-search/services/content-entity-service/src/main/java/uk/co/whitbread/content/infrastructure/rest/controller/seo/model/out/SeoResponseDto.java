package uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoResponseDto {

  private String pageTitle;
  private String pageDescription;
  private String cardImageUrl;
  private String faviconUrl;
  private List<IconDto> icons;
  private List<MsIconDto> msIcons;
  private FaqDto faq;
  private List<HreflangDto> hreflangs;
}
