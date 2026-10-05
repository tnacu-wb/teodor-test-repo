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
public class Content {

  private List<SubNav> subNav;
  private List<Country> countries;
  private Global global;
  private Menu menu;
  private Form form;
  private Header header;
  private PrivacyPolicy privacyPolicy;
  private Authentication authentication;
  private Hero hero;
  private Results results;
  private Announcement announcement;
  private ContactBanner contactBanner;
  private Map map;
  private SkipButton skipButton;
  private Favicon favicon;
  private Seo seo;
}
