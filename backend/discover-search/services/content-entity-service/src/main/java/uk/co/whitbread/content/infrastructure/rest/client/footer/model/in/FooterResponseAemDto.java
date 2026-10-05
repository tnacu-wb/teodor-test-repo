package uk.co.whitbread.content.infrastructure.rest.client.footer.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FooterResponseAemDto {

  private List<LinkTabsAemDto> linkTabs;
  private List<SocialLinksAemDto> socialLinks;
  private String copyright;
  private NewsletterSignupAemDto newsletterSignup;
  private List<LinkItemsAemDto> bottomLinks;

}
